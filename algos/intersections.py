import osmnx as ox, networkx as nx
from IPython.display import IFrame
import random
import json
import math

ox.config(log_file=True, log_console=True, use_cache=True)

def getParcours(distance, latitude, longitude):
	# create a network around some (lat, lon) point and plot it
	location_point_id=5204021270
	location_point_coord = (latitude, longitude)

	# Define de distance of exploration
	G = ox.graph_from_point(location_point_coord, distance=300, simplify=False)
	# This is only to visualise the point in a graph t 


	######################### Simplify the graph (have only the intersections) ####################################

	G2 = G.copy()
	G2 = ox.simplify_graph(G2)
	#fig, ax = ox.plot_graph(G2, node_color='b', node_zorder=3)

	#This if only if you need the lat and long

	nodes = ox.graph_to_gdfs(G2, edges=False)
	nodes[['x', 'y']]

	#######SHORTEST PATH#############

	origin_node = list(G2.nodes())[0]
	destination_node = list(G2.nodes())[9]
	route = nx.shortest_path(G2, origin_node, destination_node)
	#route_map = ox.plot_route_folium(G2, route)
	#filepath = 'data/route.html'
	#route_map.save(filepath)
	#IFrame(filepath, width=600, height=500)


	d=[route]
	############CALCUL DU PATH#########
	#distance=10000
	divisions=int(distance/200)
	parcour=0
	length=0
	route=[]
	print(divisions)
	testlocation_id=5204021270
	testLocation_coord = (latitude, longitude)
	origintest={'y':latitude , 'x': longitude , 'osmid':5204021270}
	origintest2=list(G2.nodes())[0]
	lx=[]
	ly=[]
	for x in range(1,divisions+1):
		xcoord=G2.node[origintest2]['x']
		ycoord=G2.node[origintest2]['y']
		ori_coord=(ycoord, xcoord)
		G = ox.graph_from_point(testLocation_coord, distance=300, simplify=False)
		G2 = G.copy()
		G2 = ox.simplify_graph(G2)
		#This if only if you need the lat and long

		nodesTest = ox.graph_to_gdfs(G2, edges=False)
		nodesTest[['x', 'y']]
		destination_node = list(G2.nodes())[random.randint(1,len(G2.nodes())-1)]
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
		for j in range(0,len(route2)):
			ly.append(G2.node[route2[j]]['y'])
			lx.append(G2.node[route2[j]]['x'])
			pass
		print(route2)

		#fig, ax = ox.plot_graph_route(G2, route2)
		route= route + route2
		parcour=parcour+ distancenoeuds
		origintest2=destination_node


		pass
	#Return to starting node
	if destination_node!=origin_node:
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
		for j in range(0,len(route2)):
			ly.append(G2.node[route2[j]]['y'])
			lx.append(G2.node[route2[j]]['x'])
			pass
		print(route2)
		#fig, ax = ox.plot_graph_route(G2, route2)
		route=route+route2
		parcour=parcour+ distancenoeuds
		pass




	#PRINT THE RESULTS
	print(nodes)
	print(list(G2.nodes()))

	ids=[]
	for x in range(0,len(list(G2.nodes()))):
		id=list(G2.nodes())[x]
		lat=G2.node[id]
		ids.append(id)
		print(lat)
		pass
	print(ids)
	############################## PRINT FOR TESTS ##############################
	print('ROUTE DU PLUS COURT CHEMIN')
	print(route)
	print(d)
	print('DISTANCE')
	print(parcour)
	print(origin_node)
	print(type(ori_coord))
	print(type(testLocation_coord))

	############################## CORDINATES LIST GENERATION##############################
	routelat=[]
	for n in range(0,len(lx)):
		routelat.append('0')

	for n in range(0,len(lx)):
		routelat[n]="("+str(ly[n])+','+str(lx[n])+")"
		pass
	############################## PRINT in a .txt ##############################
	print(routelat)
	with open('data.txt', 'w') as outfile:  
		json.dump(routelat, outfile)