#! /usr/bin/python
# -*- coding:utf-8 -*-

from flask import Flask
from flask import render_template
from flask import request
import os
import json

app = Flask(__name__)

@app.route('/', methods=['GET', 'POST'])
def contact():
    if request.method == 'GET':
        return "coucou"

@app.route('/coord', methods=['GET', 'POST'])
def getCoord():
    if request.method == 'GET':
        return json.dumps({'latitude':45.75, 'longitude':4.85})

   
if __name__ == '__main__':
    port = int(os.environ.get("PORT", 5000))
    app.run(host='localhost', debug=True, port=port)


