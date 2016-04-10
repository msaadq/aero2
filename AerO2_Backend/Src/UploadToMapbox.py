import ResultToGeoJson
import AzureSQLHandler
import DataBaseLayer
import mapbox
import os

'''
Upload a specific table's data in the form of geojson files to mapbox
'''


class UploadToMapbox:
    azureSQL = AzureSQLHandler.AzureSQLHandler()
    database = DataBaseLayer.DataBaseLayer()
    table_name = ""
    minAirIndex = 0
    maxAirIndex = 1024
    divisionInterval = 0
    uploader = mapbox.Uploader(
        access_token='sk.eyJ1IjoibXVkZGFzc2lyMjM1IiwiYSI6ImNpanVnYm1kaTBmZWx0d2tzY2lhYW9oYzIifQ.Thq7Zk5E-xLnozjEB5giLQ')

    '''
    Divides the table's data in to e.g. 10 divisions in acsending order e.g. sets of smog values
    from 0to50, 50to102 and so on and uploads those 10 files to mapbox so they can be treated as
    layers.

    @params: self, Table Name (String), Divisions (Int)
    @return: None
    '''

    def __init__(self, table_name, divisions=11):

        self.table_name = table_name
        self.divisionInterval = (self.maxAirIndex - self.minAirIndex) / divisions
        itertator = 1

        for x in range(0, self.maxAirIndex, self.divisionInterval):
            geoJson = ResultToGeoJson.GeoJson()

            # make the specific query
            if (self.table_name == self.database.RESULTS_TABLE_NAME):
                whereQuery = " air_index " + " >= " + str(x) + " AND " + "air_index <= " + str(
                    x + self.divisionInterval - 1)
            elif (self.table_name == self.database.SAMPLE_TABLE_NAME):
                whereQuery = " smog " + " >= " + str(x) + " AND " + "smog <= " + str(x + self.divisionInterval - 1)

            tuples = self.azureSQL.select_data(self.table_name, " * ", whereQuery)
            print(tuples)

            # if there where any tuples in this range
            if tuples:
                # make a new file with a name e.g. AirSampleDataTable1.geojson represents a file containing the lowest smog values
                newfile = open("Air" + self.table_name[6:] + str(itertator) + ".geojson", "w", )

                # append all the tuples in to geojson string
                for tuple in tuples:
                    if (self.table_name == self.database.RESULTS_TABLE_NAME):
                        geoJson.addAirTuple(float(tuple[4]), float(tuple[5]), float(tuple[6]), float(tuple[7]))
                    elif (self.table_name == self.database.SAMPLE_TABLE_NAME):
                        geoJson.addAirTuple(float(tuple[4]), float(tuple[6]), float(tuple[5]), float(tuple[8]))

                # save the geojson string in the geojson file
                newfile.write(geoJson.makeGeoJson())
                newfile.close()

                # open that file
                data = open("Air" + self.table_name[6:] + str(itertator) + ".geojson", 'rb')
                # and upload it to mapbox
                response = self.uploader.upload(data,
                                                tileset='muddassir235.' + "Air" + self.table_name[6:] + str(itertator),
                                                name="Air" + self.table_name[6:] + str(itertator))
                print(response)
                data.close()

            # if the query returned no tuples
            else:
                # make a new file of the range queried
                newfile = open("Air" + self.table_name[6:] + str(itertator) + ".geojson", "w", )

                # add a default point in antarctica
                newfile.write(
                    "{\"type\": \"FeatureCollection\", \"features\": [{  \"type\": \"Feature\", \"properties\": {}, \"geometry\": { \"type\": \"Point\", \"coordinates\": [26.103515625,-84.95930495623834]}}]}")
                newfile.close()
                data = open("Air" + self.table_name[6:] + str(itertator) + ".geojson", 'rb')
                # and upload it to mapbox
                response = self.uploader.upload(data,
                                                tileset='muddassir235.' + "Air" + self.table_name[6:] + str(itertator),
                                                name="Air" + self.table_name[6:] + str(itertator))
                print(response)
                data.close()
            itertator = itertator + 1

        # delete all the files that where made in the last step
        for x in range(1, divisions + 1):
            os.remove("Air" + self.table_name[6:] + str(x) + ".geojson")
