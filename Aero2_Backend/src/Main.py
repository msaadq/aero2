import DataTraining as dt
import Maps as map

city_name = "Islamabad"

data_training = dt.DataTraining()

all_coordinates = data_training._get_all_coordinates(map.get_corner_coordinates(city_name))


for i in range(0, 100):
    print data_training._calc_distance_on_unit_sphere(all_coordinates[i][0], all_coordinates[i][1], all_coordinates[i + 1][0], all_coordinates[i + 1][1])