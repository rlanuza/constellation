from math import sqrt, pow, log10
import random

main_bodies = {
    'EARTH': '# Earth system',
    'MARS': '# Mars system',
    'JUPITER': '# Jupiter system',
    'SATURN': '# Saturn system',
    'URANUS': '# Uranus system',
    'NEPTUNE': '# Neptune system',
    'PLUTO': '# Pluto system',
    '1976_Aten': '# Aten asteroid',
    'Voyager 1 (spacecraft)': '# Spacecraft',
    '1P/Halley': '# Comet',
    'Tesla Roadster (AKA Starman, 2018-017A)': 'Other'
}


def export_config(head_file, out_file, bodies):
    content = __get_header(head_file) + '\n'
    random.seed(1234)
    for body in bodies['bodies']:
        for key, value in body.items():
            name = value['name']
            speed = value['speed']
            mass = value['mass']
            # Insert main bodies comment
            if name in main_bodies: content += main_bodies[name] + '\n'
            print('Name: %s, mass:%E' % (name, mass))
            color_r = min(int(log10(mass) * 9 + random.randint(64, 200)), 255)
            color_g = min(int(log10(mass) * 9 + random.randint(64, 200)), 255)
            color_b = min(int(log10(mass) * 9 + random.randint(64, 200)), 255)

            if sqrt(pow(speed[0], 2) + pow(speed[1], 2) + pow(speed[2], 2)) > 0.0:
                s = '%-16s, ' % (value['name']) \
                    + '% .17E, % .17E, ' % (mass, value['radius']) \
                    + '% .17E, % .17E, % .17E, ' % (value['position'][0], value['position'][1], value['position'][2]) \
                    + '% .17E, % .17E, % .17E, ' % (speed[0], speed[1], speed[2]) \
                    + '%i, %i, %i\n' % (color_r, color_g, color_b)
            else:
                s = ''
        content += s

        with open(out_file, 'w') as the_file:
            the_file.write(content)


def __get_header(head_file):
    with open(head_file) as file:
        return file.read()
