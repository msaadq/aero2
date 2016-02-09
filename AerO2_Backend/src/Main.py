import googlemaps as gmaps

geocoder = gmaps.Client(key="AIzaSyAxC9UsA68-zYS6aSsjCG5Mi8WDYP3Dxd4")
geocode_result = geocoder.geocode('Islamabad')
bounds = geocode_result[0]['geometry']['bounds']

north_east = [bounds['northeast']['lat'], bounds['northeast']['lng']]
south_west = [bounds['southwest']['lat'], bounds['southwest']['lng']]


print north_east
print south_west