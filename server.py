#! /usr/bin/python
# -*- coding:utf-8 -*-

from flask import Flask
from flask import render_template
from flask import request

app = Flask(__name__)

@app.route('/', methods=['GET', 'POST'])
def contact():
    if request.method == 'GET':
        return "coucou"
   
if __name__ == '__main__':
    app.run(debug=True)
