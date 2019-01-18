def sendCoord(file):
    coordFile = open(file,"r")
    contenu = coordFile.read()
    coordFile.close()
    return contenu
