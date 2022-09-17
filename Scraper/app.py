from flask import Flask, render_template, request, session
app = Flask(__name__)
app.secret_key = 'Please'
from scraper import scrape_github, github_api

@app.route('/')
def input():
    session['HowTo'] = ''
    return render_template('choose.html')

@app.route('/API')
def API():
    session['HowTo'] = 'API'
    return render_template('API.html')

@app.route('/Scrape')
def scraper():
    session['HowTo'] = 'Scrape'
    return render_template('scraper.html')

@app.route('/output',methods = ['POST', 'GET'])
def result():
    if request.method == 'POST':
        if session['HowTo'] == 'Scrape':
            result = request.form
            answer = scrape_github(result["search_term"], 1)
            return render_template("output.html",result = answer)
        else:
            result = request.form
            answer = github_api(result["search_term"], result["num_pages"])
            return render_template("output.html",result = answer)


if __name__ == '__main__':
    app.run()
