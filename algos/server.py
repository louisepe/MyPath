#! /usr/bin/python
# -*- coding:utf-8 -*-

from flask import Flask
from flask import render_template
from flask import request
import os
import json
from intersections import getParcours
from readFile import sendCoord

app = Flask(__name__)

@app.route('/', methods=['GET', 'POST'])
def contact():
    if request.method == 'GET':
        return "coucou"

@app.route('/coord', methods=['GET', 'POST'])
def getCoord():
    if request.method == 'GET':
        return sendCoord("data.txt")

@app.route('/infosUser', methods=['POST'])
def getInfos():
    if request.method == 'POST':
            data = request.json
            print(data)
            latitude = round(data["latitude"],3)
            longitude = round(data["longitude"],3)
            print (latitude)
            getParcours(data["distance"],latitude, longitude )
            return "ok"
        

if __name__ == '__main__':
    port = int(os.environ.get("PORT", 5000))
    app.run(host='localhost', debug=True, port=port)


