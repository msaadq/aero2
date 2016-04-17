import DataBaseLayer
import UploadToMapbox as utm

database=DataBaseLayer.DataBaseLayer()
utm.UploadToMapbox(database.RESULTS_TABLE_NAME)
utm.UploadToMapbox(database.SAMPLE_TABLE_NAME)
