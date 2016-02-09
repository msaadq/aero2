import DataTraining as dt

CITY_NAME = "Islamabad"  # Default city name for AerO2 application

if __name__ == '__main__':

    data_training = dt.DataTraining()

    # Initializes the Database by saving all the required node properties
    data_training.initialize(CITY_NAME);

    # Updates the outputs in the database after interpolation
    data_training.update_database()

