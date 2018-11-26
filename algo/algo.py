import requests as req
import json

STARTPOS = (45.7837389,4.8725722)
URLAPI ="http://overpass-api.de/api/interpreter?data=[out:json];"

################################################################################
###################### Selection du noeud de départ ############################
################################################################################

def startingNode(startPos):
    # définition de la zone de recherche pour le noeud de départ
    startArea = (startPos[0]-0.00003, startPos[1]-0.00003, startPos[0]+0.00003, startPos[1]+0.00003)

    # Création de la requête récupérant les noeud de la zone
    query = "node"+ str(startArea) +";out body;"
    url = URLAPI + query
    #print(url)

    # Appel à l'API
    content = req.get(url)
    data = content.json()

    return data['elements'][0]


################################################################################
############################ Start sur un chemin ###############################
################################################################################

def wayify(noeud):
    # Création da la requète récupérant le chemin du noeud
    query1 = "node("+ str(noeud['id']) +");way(bn);out body;"
    url1 = URLAPI + query1

    # Appel à l'API
    content1 = req.get(url1)
    data1 = content1.json()

    # Vérification que le noeud n'est pas sur un chemin
    if(data1['elements']==[]):
        # Le noeud n'est pas sur un chemin
        # Création da la requète récupérant les chemin à moins de 200m du noeud
        query2 = "node("+ str(noeud['id']) +");way(around:200);out body;"
        url2 = URLAPI + query2

        # Appel à l'API
        content2 = req.get(url2)
        data2 = content2.json()

        # Nouveau noeud (la première du chemin)
        newNode = data2['elements'][0]['nodes'][0]

        # Création da la requète récupérant les information du noeud
        query3 = "node("+ str(newNode) +");out body;"
        url3 = URLAPI + query3

        # Appel à l'API
        content3 = req.get(url3)
        data3 = content2.json()

        return data3['elements']

    else :
        # Le noeud est déjà sur un chemin
        return noeud

################################################################################
###################### Récupération des noeuds suivants #########################
################################################################################

def getNextNodes(noeud):
    # Création de la requète récupérant les noeud au bout des chemins
    query = "node("+ str(noeud['id']) +");way(bn);out body;"
    url = URLAPI + query

    # Appel à l'api
    content = req.get(url)
    data = content.json()

    # Sauvegarde des chemins
    ways = data['elements']

    # Liste des neuds vide
    nodes = []
    # Parcours des chemins
    for way in ways:
        # Ajout des extrémités à la liste des noeuds
        nodes.append(way['nodes'][0])
        nodes.append(way['nodes'][len(way['nodes'])-1])

    return nodes

################################################################################
################################## Tests #######################################
################################################################################
startnode = startingNode(STARTPOS)
print("Start node initiale : ")
print(startnode)
print()

startnode = wayify(startnode)
print("Start node sur un chemin : ")
print(startnode)
print()

nextNodes = getNextNodes(startnode)
print("Nodes suivantes : ")
print(nextNodes)
