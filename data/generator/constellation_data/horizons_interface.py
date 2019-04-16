from astroquery.jplhorizons import Horizons
import re

# G= 6.67408(31)×10−11    m3⋅kg−1⋅s−2
G = 6.6740831e-11 / 1e9  # km3⋅kg−1⋅s−2

factor_AU_km = 149597870.7 # https://en.wikipedia.org/wiki/Astronomical_unit
factor_AU_day_km_s = factor_AU_km / (24 * 60 * 60)

reco_position = re.compile(r'.*\$\$SOE(.*)\$\$EOE.*', re.MULTILINE | re.DOTALL)
reco_mass = re.compile(r'GM[^=g]*?=\s*(\d+\.\d+)')
reco_radius0 = re.compile(r'Vol. Mean Radius[^=]*?=\s*(\d+\.?\d*)', re.IGNORECASE)
reco_radius1 = re.compile(r'RAD=\s*((\d*)\.(\d*)|(\d+))')
reco_radius2 = re.compile(r'^\s*Radius.*km.*?=\s*((\d*)\.(\d*)|(\d+))', re.MULTILINE | re.IGNORECASE)
reco_name = re.compile(r'^Target body name:\s*(\S+\s*\S*\s*\S*\s*\S*\s*\S*)\s+{.+$', re.MULTILINE)


def update_bodies(bodies_raw):
    for body in bodies_raw['bodies']:
        for naif_id_key, value in body.items():
            name = value['name']
            value['rgb'] = [255, 255, 255]
            obj = Horizons(id=naif_id_key, location='500@0',
                           epochs={'start': '2018-03-06',
                                   'stop': '2018-03-07',
                                   'step': '1d'}, id_type='majorbody', )
            s_vec = obj.vectors(get_raw_response=True)
            print(name)
            # Get name
            value['name_horizons'] = __get_name(s_vec, value['name'])
            # Get mass
            value['mass'] = __get_mass(s_vec, value['mass'])
            # Get radius
            value['radius'] = __get_radius(s_vec, value['radius'])
            # Get position
            __get_position(s_vec, value['position'], value['speed'])


def __get_name(s_vec, default_name=''):
    match = reco_name.search(s_vec)
    if match:
        name = match.group(1).strip()
    else:
        name = default_name
    return name


def __get_radius(s_vec, default_radius=-1.0):
    match = reco_radius0.search(s_vec)
    if match:
        radius = float(match.group(1)) * 1000
    else:
        match = reco_radius1.search(s_vec)
        if match:
            radius = float(match.group(1)) * 1000
        else:
            match = reco_radius2.search(s_vec)
            if match:
                radius = float(match.group(1)) * 1000
            else:
                radius = default_radius
    return radius


def __get_mass(s_vec, default_mass=-1.0):
    match = reco_mass.search(s_vec)
    if match:
        mass = float(match.group(1)) / G
    else:
        mass = default_mass
    return mass


def __get_position(s_vec, position, speed):
    match = reco_position.search(s_vec)
    if match:
        pos_and_speed = match.group(1).split(',')
        position[0] = float(pos_and_speed[2]) * factor_AU_km
        position[1] = float(pos_and_speed[3]) * factor_AU_km
        position[2] = float(pos_and_speed[4]) * factor_AU_km
        speed[0] = float(pos_and_speed[5]) * factor_AU_day_km_s
        speed[1] = float(pos_and_speed[6]) * factor_AU_day_km_s
        speed[2] = float(pos_and_speed[7]) * factor_AU_day_km_s


def show(obj, na_body='---'):
    # print("------------------------------------------------------------------------------------------")
    # eph = obj.ephemerides()
    # print(eph.columns)
    s_vec = obj.vectors(get_raw_response=True)
    print(s_vec)


def show_bodies1(range_max):
    for body in range(range_max):
        id_body = body
        obj = Horizons(id=id_body, location='500@0',
                       epochs={'start': '2018-03-06',
                               'stop': '2018-03-07',
                               'step': '1d'},
                       id_type='majorbody', )
        show(obj)


def show_bodies0():
    bodies = ['Sun:10', 'Mercury:199', 'Venus:299', 'Earth:399', 'Moon:301',
              'Mars:499', 'Ceres:Ceres', 'Jupiter:599', 'Saturn:699', 'Uranus:799',
              'Neptune:899', 'Pluto:999', 'Voyager 1 (spacecraft):-31', 'Voyager 2 (spacecraft):-32',
              '1P/Halley:90000033', 'Hale-Bopp (C/1995 O1):90002027', '67P/Churyumov-Gerasimenko:90000685',
              'Tesla Roadster (AKA: Starman, 2018-017A):Tesla', 'Makemake (2005 FY9):136472',
              'Eris (2003 UB313):136199', 'Bennu (1999 RQ36):101955', '(2015 BZ509): 514107',
              "1I/'Oumuamua:A/2017 U1", 'Charon:901',
              ]

    for body in bodies:
        na_body = body.split(':')[0]
        id_body = body.split(':')[1]
        obj = Horizons(id=id_body, location='500@0',
                       epochs={'start': '2018-03-06',
                               'stop': '2018-03-07',
                               'step': '1d'},
                       id_type='majorbody', )
        show(obj, na_body)
