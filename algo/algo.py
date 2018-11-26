import requests as req
import json

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
        print()
        print("---------ONWAY---------")
        return True
    else :
        print()
        print("---------NOT ONWAY------------")
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

    # Liste des neuds vide
    nodes = []
    # Parcours des chemins
    for way in ways:
        # Faire attention a ne pas tomber dans une boucle infinie
        print(way['nodes'][0])
        print(way['nodes'][len(way['nodes'])-1])
        if(way['nodes'][0]!=way['nodes'][len(way['nodes'])-1]):
            # Ajout des extrémités à la liste des noeuds
            print()
            print("--------NOT LOOP----------")
            return False

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
    print("Toutes les nodes")
    print(nodes)

    # Recherche d'un bon noeud de départ
    for node in nodes:
        print("Node en cours : ")
        print(node)
        if(onWay(node)==True and onLoopWay(node)==False):
            return node

    # Si aucun noeud n'a été trouvé on recommence avec un rayon plus large
    node = startingNode(startPos, radius*2)

    return node

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
        query2 = "node("+ str(noeud['id']) +");way(around:100);out body;"
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
###################### Récupération des noeuds suivants ########################
################################################################################

def getNextNodes(noeud):
    # Création de la requète récupérant les noeud au bout des chemins
    query = "node("+ str(noeud['id']) +");way(bn);out body;"
    url = URLAPI + query
    print(url)

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



    newNode = noeud
    # Si aucun noeuds suivant n'a été trouvé
    if(nodes==[]):
        # On trouve un autre noeud proche sur un chemin
        newNode = getCloseNodeOnWay(noeud)
        print("ECHEC NOUVELLE BOUCLE, NOUVELLE NODE :")
        print(newNode)
        # On relance cette fonction avec le nouveau noeud
        nodes = getNextNodes(newNode)

    #On retourne les nodes qui viennent après newNode
    print(nodes)
    return nodes, newNode

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
############## Récupération d'un noeud proche sur un chemin ####################
################################################################################

def getCloseNodeOnWay(noeud):
    # Création de la requête récupérant le noeud
    query = "node("+ str(noeud['id']) +");node(around:20);out body;"
    url = URLAPI + query

    # Appel à l'API
    content = req.get(url)
    data = content.json()

    newNode = data['elements'][1]

    newNode = wayify(newNode)

    return newNode

################################################################################
################################## Tests #######################################
################################################################################
startnode = startingNode(STARTPOS, RADIUS)
print("Start node initiale : ")
print(startnode)
print()

nextNodes = []
nextNodes = nextNodes + getNextNodes(startnode)[0]
print("Nodes suivantes : ")
print(nextNodes)


nextNode = getNodeInfo(nextNodes.pop(1))
nextNodes = nextNodes + getNextNodes(nextNode)[0]
print(nextNodes)
