import requests as req
import json
import time
import math

STARTPOS = (45.7837389,4.8725722)
RADIUS = 0.0001
URLAPI ="http://overpass-api.de/api/interpreter?data=[out:json];"

################################################################################
############################ Sur un chemin #####################################
################################################################################

def onWay(noeud):
    # Création da la requète récupérant le chemin du noeud
    query = "node("+ str(noeud['id']) +");way(bn);out body;"
    url = URLAPI + query

    # Appel à l'API
    content = req.get(url)
    data = content.json()

    # Vérification que le noeud est sur un chemin
    if(data['elements']!=[]):
        # print()
        # print("Noeud : "+ str(noeud))
        # print("---------ONWAY---------")
        return True
    else :
        # print()
        # print("Noeud : "+ str(noeud))
        # print("---------NOT ONWAY------------")
        return False

################################################################################
######################### Sur un chemin qui boucle #############################
################################################################################

def onLoopWay(noeud):
    # Création da la requète récupérant le chemin du noeud
    query = "node("+ str(noeud['id']) +");way(bn);out body;"
    url = URLAPI + query

    # Appel à l'API
    content = req.get(url)
    data = content.json()

    # Sauvegarde des chemins
    ways = data['elements']

    # print()
    # print("Noeud : "+ str(noeud))
    # Liste des neuds vide
    nodes = []
    # Parcours des chemins
    for way in ways:
        # Faire attention a ne pas tomber dans une boucle infinie
        # print(way['nodes'][0])
        # print(way['nodes'][len(way['nodes'])-1])
        if(way['nodes'][0]!=way['nodes'][len(way['nodes'])-1]):
            # Ajout des extrémités à la liste des noeuds
            # print("--------NOT LOOP----------")
            return False

    # print("--------LOOP WAY----------")
    return True

################################################################################
###################### Selection du noeud de départ ############################
################################################################################

def startingNode(startPos, radius):
    # définition de la zone de recherche pour le noeud de départ
    startArea = (startPos[0]-radius, startPos[1]-radius, startPos[0]+radius, startPos[1]+radius)

    # Création de la requête récupérant les noeud de la zone
    query = "node"+ str(startArea) +";out body;"
    url = URLAPI + query

    # Appel à l'API
    content = req.get(url)
    data = content.json()

    # Sauvegarde des noeuds
    nodes = data['elements']
    print("Radius : "+ str(radius))
    # print()
    # print(nodes)

    # Recherche d'un bon noeud de départ
    for node in nodes:
        # print()
        # print("Node en cours : ")
        # print(node)
        if(onWay(node)==True and onLoopWay(node)==False):
            return node

    # Si aucun noeud n'a été trouvé on recommence avec un rayon plus large
    node = startingNode(startPos, radius*2)

    return node

################################################################################
###################### Récupération des noeuds suivants ########################
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
        # Faire attention a ne pas tomber dans une boucle infinie
        if(way['nodes'][0]!=way['nodes'][len(way['nodes'])-1]):
            # Ajout des extrémités à la liste des noeuds
            nodes.append(way['nodes'][0])
            nodes.append(way['nodes'][len(way['nodes'])-1])

    return nodes

################################################################################
###################### Récupération des info d'un noeud ########################
################################################################################

def getNodeInfo(noeudId):
    # Création de la requête récupérant le noeud
    query = "node("+ str(noeudId) +");out body;"
    url = URLAPI + query

    # Appel à l'API
    content = req.get(url)
    data = content.json()

    return data['elements'][0]

################################################################################
#################### Calcul de la distance entre 2 noeuds ######################
################################################################################

def distanceBetween(noeud1, noeud2):
    lat1 = noeud1['lat']
    lat2 = noeud2['lat']
    lon1 = noeud1['lon']
    lon2 = noeud2['lon']

    distance = 1852*60*math.acos(math.sin(lat1)*math.sin(lat2)+math.cos(lat1)*math.cos(lat2)*math.cos(lon2-lon1))
    return distance

################################################################################
#################### Mise à jour des tableau d'exploration #####################
################################################################################

def updateExploration(notExplored, explored):
    # Récupération du noeud à explorer
    exploreNode = notExplored.pop(0)
    # Récupération des noeuds suivant
    nextNodes = getNextNodes(exploreNode)

    # Parcours des noeuds suivant
    for noeud in nextNodes:
        if(noeud!=exploreNode['id']):
            if(len(explored)>0):
                if(noeud!=explored[exploreNode['parentIndex']]['id']):

                    # Récupération de toutes les infos du noeud
                    node = getNodeInfo(noeud)
                    # Récupération de la distance entre le noeud exploré et ce noeud
                    distance = distanceBetween(exploreNode, node)
                    # Ajout de la distance de noeud depuis le départ
                    node['distance'] = exploreNode['distance'] + distance
                    # Ajout de l'index du noeud parent
                    node['parentIndex'] = len(explored)
                    if(len(list(filter(lambda alreadyNode: alreadyNode['id']==noeud, notExplored)))>0):
                        if(abs(list(filter(lambda alreadyNode: alreadyNode['id']==noeud, notExplored))[0]['distance']-node['distance'])>20):
                            # Ajout du noeud à la liste des noeuds à explorer
                            notExplored = notExplored + [node]
                    else:
                        # Ajout du noeud à la liste des noeuds à explorer
                        notExplored = notExplored + [node]
            else:
                # Récupération de toutes les infos du noeud
                node = getNodeInfo(noeud)
                # Récupération de la distance entre le noeud exploré et ce noeud
                distance = distanceBetween(exploreNode, node)
                # Ajout de la distance de noeud depuis le départ
                node['distance'] = exploreNode['distance'] + distance
                # Ajout de l'index du noeud parent
                node['parentIndex'] = len(explored)
                # Ajout du noeud à la liste des noeuds à explorer
                notExplored = notExplored + [node]

    # Tri des noeuds par ordre croissant de distance
    notExplored = sorted(notExplored, key = lambda node: node['distance'])
    # Ajout du noeud exploré à la liste des noeuds déjà explorés
    explored = explored + [exploreNode]

    # Renvoie des deux tableaux mis à jour
    return notExplored, explored

################################################################################
################################## Tests #######################################
################################################################################

# Récupération du noeud de départ
startnode = startingNode(STARTPOS, RADIUS)
startnode['distance'] = 0
startnode['parentIndex'] = None
print("Start node : ")
print(startnode)

# Initialisation du tableau des noeuds à explorer
nodeToExplore = [startnode]
# Initialisation du tableau des noeuds explorés
nodeExplored = []
#
# nodeToExplore, nodeExplored = updateExploration(nodeToExplore, nodeExplored)
# print("Noeuds à explorer : ")
# print(nodeToExplore)
# print("Noeuds explorés :")
# print(nodeExplored)
i=1
while nodeToExplore[0]['distance']<1000 :
    print()
    nodeToExplore, nodeExplored = updateExploration(nodeToExplore, nodeExplored)
    print("Noeuds à explorer : ")
    print(nodeToExplore)
    print("Noeuds explorés :")
    print(nodeExplored)
