import requests as req
import json
import time
import math

STARTPOS = (45.7837389,4.8725722)
RADIUS = 0.0001
URLAPI ="http://overpass-api.de/api/interpreter?data=[out:json];"
DISTANCE = 500

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

    sin1 = math.sin(math.radians(lat1))
    sin2 = math.sin(math.radians(lat2))
    cos1 = math.cos(math.radians(lat1))
    cos2 = math.cos(math.radians(lat2))
    cos3 = math.cos(math.radians(lon2-lon1))

    # A cause des problème d'arrondi on a parfois un distance de 0
    if(((sin1*sin2) + (cos1*cos2*cos3))>1):
        # == distance = 0
        distance = 6367445*math.acos(1)
    else:
        distance = 6367445*math.acos((sin1*sin2) + (cos1*cos2*cos3))
    return distance

################################################################################
################## Mise à jour des tableau d'exploration BFS ###################
################################################################################

def updateExplorationBFS(notExplored, explored):
    # Récupération du noeud à explorer
    exploreNode = notExplored.pop(0)
    # Récupération des noeuds suivant
    nextNodes = getNextNodes(exploreNode)

    # Parcours des noeuds suivant
    for noeud in nextNodes:
        # Récupération de toutes les infos du noeud
        node = getNodeInfo(noeud)
        # Récupération de la distance entre le noeud exploré et ce noeud
        distance = distanceBetween(exploreNode, node)
        # Ajout de la distance de noeud depuis le départ
        node['distance'] = exploreNode['distance'] + distance
        # Ajout de l'index du noeud parent
        node['parentIndex'] = len(explored)
        # Ajout des noeuds frère
        node['noeudsFrere'] = nextNodes
        # Ajout des nodes interdites
        node['noeudsdInterdits'] = [exploreNode['id']]+node['noeudsFrere']

        # Si le noeud ne fait pas partie des noeuds interdit d'exploration
        if((noeud in exploreNode['noeudsdInterdits'])==False):
            # Si le noeud n'est pas un noeud par lequel on est déjà passé il y a moin de 100m
            if(len(list(filter(lambda alreadyNode: alreadyNode['id']==noeud, notExplored)))>0):
                if(abs(list(filter(lambda alreadyNode: alreadyNode['id']==noeud, notExplored))[0]['distance']-node['distance'])>100):
                    # Ajout du noeud à la liste des noeuds a exploré
                    notExplored = notExplored + [node]
            else :
                # Ajout du noeud à la liste des noeuds a exploré
                notExplored = notExplored + [node]

    # Tri des noeuds par ordre croissant de distance
    notExplored = sorted(notExplored, key = lambda node: node['distance'])
    # Ajout du noeud exploré à la liste des noeuds déjà explorés
    explored = explored + [exploreNode]

    # Renvoie des deux tableaux mis à jour
    return notExplored, explored

################################################################################
################# Mise à jour des tableau d'exploration BFS2 ###################
################################################################################

def updateExplorationBFS2(notExplored, explored):
    # Récupération du noeud à explorer
    exploreNode = notExplored.pop(0)
    # Récupération des noeuds suivant
    nextNodes = getNextNodes(exploreNode)

    # Parcours des noeuds suivant
    for noeud in nextNodes:
        # Récupération de toutes les infos du noeud
        node = getNodeInfo(noeud)
        # Récupération de la distance entre le noeud exploré et ce noeud
        distance = distanceBetween(exploreNode, node)
        # Ajout de la distance de noeud depuis le départ
        node['distance'] = exploreNode['distance'] + distance
        # Ajout de l'index du noeud parent
        node['parentIndex'] = len(explored)
        # Ajout des noeuds frère
        node['noeudsFrere'] = nextNodes
        # Ajout des nodes interdites
        node['noeudsdInterdits'] = []

        endNode={}
        if(noeud==realStart['id']):
            endNode=node
        # Si le noeud ne fait pas partie des noeuds interdit d'exploration
        elif(len(list(filter(lambda alreadyNode: alreadyNode['id']==noeud, notExplored)))==0 and len(list(filter(lambda alreadyNode: alreadyNode['id']==noeud, explored)))==0 and noeud!=exploreNode['id'] ):
            notExplored = notExplored + [node]

    # Tri des noeuds par ordre croissant de distance
    notExplored = sorted(notExplored, key = lambda node: node['distance'])
    # Ajout du noeud exploré à la liste des noeuds déjà explorés
    explored = explored + [exploreNode]
    if(endNode!={}):
        explored = explored + [endNode]

    # Renvoie des deux tableaux mis à jour
    return notExplored, explored

################################################################################
################################## Tests #######################################
################################################################################

# Récupération du noeud de départ
startnode = startingNode(STARTPOS, RADIUS)
startnode['distance'] = 0
startnode['parentIndex'] = None
startnode['noeudsFrere'] = []
startnode['noeudsdInterdits'] = []
print("Start node : ")
print(startnode)

# Initialisation du tableau des noeuds à explorer
nodeToExplore = [startnode]
# Initialisation du tableau des noeuds explorés
nodeExplored = []
startsnode, nodeExplored = updateExplorationBFS(nodeToExplore, nodeExplored)
nodeToExplore = [startsnode[0]]
realStart = startsnode[0]
# nodeToExplore, nodeExplored = updateExploration(nodeToExplore, nodeExplored)
# print("Noeuds à explorer : ")
# print(nodeToExplore)
# print("Noeuds explorés :")
# print(nodeExplored)
i=1
while nodeToExplore[0]['distance']<DISTANCE :
    print()
    nodeToExplore, nodeExplored = updateExplorationBFS2(nodeToExplore, nodeExplored)
    print("Noeuds à explorer : ")
    print(nodeToExplore)
    print("Noeuds explorés :")
    print(nodeExplored)
#     if(nodeToExplore[0]['distance']>i*100):
#         print(str(i*100)+" m")
#         i+=1
#
# print("Noeuds explorés :")
# print(nodeExplored)
print(list(filter(lambda alreadyNode: alreadyNode['id']==nodeExplored[0]['id'], nodeExplored)))
print(list(filter(lambda alreadyNode: alreadyNode['id']==3252041060, nodeExplored)))
