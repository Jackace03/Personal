import requests
import urllib.parse
import os
from bs4 import BeautifulSoup
from github import Github

# Scrapes github repos from the given search terms

# Parameters
# -----------
# search_term : str
#   The search term for the github repositories
# num_pages : int
#   The number of pages to scrape

# Returns
# -------
# scraped_info : list
#   A list of dictionaries containing the necessary information from the scraped repos

def scrape_github(search_term, num_pages):
    result = {}
    k = 0
    stars = 0
    license = 0
    last_updated = 0
    issues = 0
    language = 0
    all_tags = []
    URL = 'https://github.com/search?q=' + urllib.parse.quote_plus(search_term)
    page = requests.get(URL)
    soup = BeautifulSoup(page.content, 'html.parser')
    results = soup.find(id='js-pjax-container')
    elems = results.find_all('div', class_ = 'mt-n1')
    for elem in elems:
        k = k + 1
        result[k] = {}
        repo_name = elem.find('a', class_ = 'v-align-middle')
        result[k]['name'] = repo_name.text.strip()
        description = elem.find('p', class_ = 'mb-1')
        if description == None:
            result[k]['description'] =  None
        else:
            result[k]['description'] =  description.text.strip()
        tags = elem.find_all('a', class_ = 'topic-tag topic-tag-link f6 px-2 mx-0')
        for tag in tags:
            all_tags.append(tag.text.strip())
        if len(all_tags) == 0:
            result[k]['tags'] =  None
        else:
            result[k]['tags'] =  all_tags
        all_tags = []
        rest = elem.find_all('div', class_ = 'mr-3')
        stars = 0
        license = 0
        last_updated = 0
        issues = 0
        language = 0
        for j in rest:
            if j.text.translate({ord(i): None for i in '.k \n'}).isnumeric():
                result[k]['num_stars'] =  j.text.strip()
                stars = 1
            elif j.text.find('license') != -1:
                if stars == 0:
                    result[k]['num_stars'] =  None
                    stars = 1
                if language == 0:
                    result[k]['language'] =  None
                    language = 1
                license = 1
                result[k]['license'] =  j.text.strip()
            elif j.text.find('Updated') != -1:
                if stars == 0:
                    result[k]['stars'] =  None
                    stars = 1
                if language == 0:
                    result[k]['language'] =  None
                    language = 1
                if license == 0:
                    result[k]['license'] =  None
                    license = 1
                date = 1
                last_updated = j.find('relative-time')['datetime']
                result[k]['last_updated'] =  last_updated
            else:
                if stars == 0:
                    result[k]['num_stars'] = None
                    stars = 1
                result[k]['language'] = j.text.strip()
                language = 1
        issues = elem.find('a', class_ = 'Link--muted f6')
        if issues != None:
            x = issues.text.strip().split()
            num_issues = x[0]
            result[k]['num_issues'] = num_issues
        else:
            result[k]['num_issues'] = None
    return result

# Searches for repos with the given search term using the GitHub REST API

# Parameters
# ----------
# search_term : str
#   The search term for the github repositories
# num_pages : int
#   The number of pages required to query for repositories

# Returns
# -------
# repo_info : list
#   A list of dictionaries containing the necessary info from the repositories

def github_api(search_term, num_pages):
    result = {}
    max_size = 10 * int(num_pages);
    k = 0
    Access_token = '4e401072a9d5bd7d1e956f7d51e10f8bbec5232c'
    g = Github(Access_token)
    query = urllib.parse.quote_plus(search_term)
    results = g.search_repositories(query)
    if (results.totalCount > max_size):
        results = results[:max_size]
    for repo in results:
        k = k + 1
        result[k] = {}
        result[k]['name'] = repo.name
        try:
            result[k]['description'] = repo.description
        except:
            result[k]['description'] = None
        result[k]['num_stars'] = repo.stargazers_count
        try:
            result[k]['language'] = repo.language
        except:
            result[k]['language'] = None
        try:
            result[k]['license'] = repo.get_license().license.name
            if result[k]['license'] == 'Other':
                result[k]['license'] = None
        except:
            result[k]['license'] = None
        result[k]['last_updated'] = repo.updated_at
        result[k]['has_issues'] = repo.has_issues
    return result
