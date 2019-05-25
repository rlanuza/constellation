echo Bodies mase Information https://naif.jpl.nasa.gov/pub/naif/toolkit_docs/FORTRAN/req/naif_ids.html
echo Coordinates read from https://ssd.jpl.nasa.gov/?horizons
python -m constellation_data --bodies=bodies.yaml --yaml_out=bodies_out.yaml --output=bodies.txt --config_txt=config.txt

timeout 60