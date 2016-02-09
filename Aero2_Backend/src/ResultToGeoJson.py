
'''
This class is for converting a set of data to the geojson (similar to kml
a way to represent geographical data which is standard a standard in mapbox).
'''

class GeoJson:
    geoJsonString="{ \"type\": \"FeatureCollection\",\"features\": [\n"

    '''
    Empty Constructor
    '''

    def __init__(self):
        pass

    '''
    Add a point to the geojson string

    @params: self, time (double), lat (double), long (double), air index (double)
    @return: None
    '''
    def addAirTuple(self, time, lat, long, airIndex):
        append=""
        if (self.geoJsonString!="{ \"type\": \"FeatureCollection\",\"features\": [\n"):
            append=append+","

        append=append+"{ \"type\": \"Feature\",\n"
        geometry="\"geometry\": \n{ \"type\": \"Point\",\n \"coordinates\":"
        geometry=geometry+  "\n["+str(long)+","+str(lat)+"]"
        geometry=geometry+  "\n}"

        append=append+geometry
        append=append+"}"
        self.geoJsonString=self.geoJsonString+append


    '''
    returns the geojson string containig all the tuples that had been appended

    @params: self
    @return: Geo Json String (String)
    '''
    def makeGeoJson(self):
        self.geoJsonString=self.geoJsonString+"\n]}\n"
        return self.geoJsonString

