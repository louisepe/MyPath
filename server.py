#! /usr/bin/python
# -*- coding:utf-8 -*-

from flask import Flask
from flask import render_template
from flask import request
import os

app = Flask(__name__)

@app.route('/', methods=['GET', 'POST'])
def contact():
    if request.method == 'GET':
        return "coucou"
   
if __name__ == '__main__':
    port = int(os.environ.get("PORT", 5000))
    app.run(host='localhost', debug=True, port=port)
