
import osmnx as ox, networkx as nx
from IPython.display import IFrame
import random
import json
import math

ox.config(log_file=True, log_console=True, use_cache=True)


# create a network around some (lat, lon) point and plot it
location_point_id=5204021270
location_point_coord = (33.2989041, -111.833341)

# Define de distance of exploration
G = ox.graph_from_point(location_point_coord, distance=200, simplify=False)
# This is only to visualise the point in a graph t 


######################### Simplify the graph (have only the intersections) THIS IS ONLY A TEST ####################################

G2 = G.copy()
G2 = ox.simplify_graph(G2)
fig, ax = ox.plot_graph(G2, node_color='b', node_zorder=3)

#This if only if you need the lat and long

nodes = ox.graph_to_gdfs(G2, edges=False)
nodes[['x', 'y']]

#######SHORTEST PATH TEST#############

origin_node = list(G2.nodes())[0]
destination_node = list(G2.nodes())[9]

#shortest path calculation
route = nx.shortest_path(G2, origin_node, destination_node)

#this is to see the path in a html file that you have to create in data/route.html
route_map = ox.plot_route_folium(G2, route)
filepath = 'data/route.html'
route_map.save(filepath)
IFrame(filepath, width=600, height=500)


d=[route]

############CALCULATION OF A CIRCULAR PATH#########
distance=10000
#the divisions is only to see how many different scan and shortest path we'll do
divisions=int(distance/200)
parcour=0
length=0

#testlocation_id=5204021270
#testLocation_coord = (33.2989041, -111.833341)
#origintest={'y':33.2989041 , 'x': -111.833341 , 'osmid':5204021270}

origintest2=list(G2.nodes())[0]
for x in range(1,divisions+1):
	
	#Simplification of the nodes, we use only intersections
	G = ox.graph_from_point(testLocation_coord, distance=300, simplify=False)
	G2 = G.copy()
	G2 = ox.simplify_graph(G2)
	#This if only if you need the lat and long

	nodesTest = ox.graph_to_gdfs(G2, edges=False)
	nodesTest[['x', 'y']]
	#use of a random destination node in the aerea
	destination_node = list(G2.nodes())[random.randint(1,len(G2.nodes())-1)]
	lat2=G2.node[destination_node]['y']
	lon2=G2.node[destination_node]['x']
	#Calculation of the distance between the 2 nodes
	lat1=G2.node[origintest2]['y']
	lon1=G2.node[origintest2]['x']
	
	sin1 = math.sin(math.radians(lat1))
	sin2 = math.sin(math.radians(lat2))
	cos1 = math.cos(math.radians(lat1))
	cos2 = math.cos(math.radians(lat2))
	cos3 = math.cos(math.radians(lon2-lon1))

	distancenoeuds = 6367445*math.acos((sin1*sin2) + (cos1*cos2*cos3))
	#route of the shortest path
	route2 = nx.shortest_path(G2, origintest2, destination_node)
	#compilation of all the nodes collected 
	route.append(route2)
	#calculation of the total distance
	parcour=parcour+ distancenoeuds
	origintest2=destination_node


	pass

#find the route to the origin point

destination_node = origin_node
lat2=G2.node[destination_node]['y']
lon2=G2.node[destination_node]['x']
lat1=G2.node[origintest2]['y']
lon1=G2.node[origintest2]['x']

sin1 = math.sin(math.radians(lat1))
sin2 = math.sin(math.radians(lat2))
cos1 = math.cos(math.radians(lat1))
cos2 = math.cos(math.radians(lat2))
cos3 = math.cos(math.radians(lon2-lon1))

distancenoeuds = 6367445*math.acos((sin1*sin2) + (cos1*cos2*cos3))
route2 = nx.shortest_path(G2, origintest2, destination_node)
route.append(route2)
parcour=parcour+ distancenoeuds



#################PRINT THE RESULTS#############################
print(nodes)
print(list(G2.nodes()))

ids=[]
#print nodes of the last graph
for x in range(0,len(list(G2.nodes()))):
	id=list(G2.nodes())[x]
	lat=G2.node[id]
	ids.append(id)
	print(lat)
	pass
print(ids)
print('ROUTE DU PLUS COURT CHEMIN')
print(route)
print(d)
print('DISTANCE')
print(parcour)
print(origin_node)


with open('data.txt', 'w') as outfile:  
    json.dump(ids, outfile)
