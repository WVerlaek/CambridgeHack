import http.client, urllib.request, urllib.parse, urllib.error, base64, sys, os
import urllib
import click

key = 'a5a4db57b7ab45f8ad477e55174106c3'

base_url = 'westcentralus.api.cognitive.microsoft.com'

headers = {
    # Request headers.
    'Content-Type': 'application/json',

    # NOTE: Replace the "Ocp-Apim-Subscription-Key" value with a valid subscription key.
    'Ocp-Apim-Subscription-Key': key,
}


@click.group()
def cli():
    pass


@cli.command(help='Create a new group')
@click.option('--group_id', prompt='Group id', help='Unique group id')
@click.option('--name', prompt='Group name')
def create_group(group_id, name):
    # Replace 'examplegroupid' with an ID you haven't used for creating a group before.
    # The valid characters for the ID include numbers, English letters in lower case, '-' and '_'.
    # The maximum length of the ID is 64.
    personGroupId = group_id

    # The userData field is optional. The size limit for it is 16KB.
    body = f"{{ 'name': '{name}'}}"

    try:
        # NOTE: You must use the same location in your REST call as you used to obtain your subscription keys.
        #   For example, if you obtained your subscription keys from westus, replace "westcentralus" in the
        #   URL below with "westus".
        conn = http.client.HTTPSConnection(base_url)
        conn.request("PUT", "/face/v1.0/persongroups/%s" % personGroupId, body, headers)
        response = conn.getresponse()

        # 'OK' indicates success. 'Conflict' means a group with this ID already exists.
        # If you get 'Conflict', change the value of personGroupId above and try again.
        # If you get 'Access Denied', verify the validity of the subscription key above and try again.
        print(response.reason)

        conn.close()
    except Exception as e:
        print(e.args)


@cli.command(help='Create person in group')
@click.option('--group_id', default='c_h', help='Unique target group id')
@click.option('--name', prompt='Person name', help='Person name')
def create_person(group_id, name):
    params = urllib.parse.urlencode({
    })

    body = f"{{'name':'{name}'}}"

    try:
        conn = http.client.HTTPSConnection(base_url)
        conn.request("POST", f"/face/v1.0/persongroups/{group_id}/persons?{params}", body, headers)
        response = conn.getresponse()
        data = response.read()
        print(f"200 OK: {data}")
        conn.close()
    except Exception as e:
        print("[Errno {0}] {1}".format(e.errno, e.strerror))


@cli.command(help="Add face to person in group from jpg source")
@click.option('--group_id', default='c_h', help='Unique group id')
@click.option('--person_id', prompt='Person name', help='Person id')
@click.option('--file_name', prompt='JPG file name', help='File name')
def add_face(group_id, person_id, file_name):
    params = urllib.parse.urlencode({
    })

    headers_local = {
        # Request headers.
        'Content-Type': 'application/octet-stream',

        # NOTE: Replace the "Ocp-Apim-Subscription-Key" value with a valid subscription key.
        'Ocp-Apim-Subscription-Key': key,
    }


    try:
        with open(file_name, 'rb') as file:
            body = file.read()
        conn = http.client.HTTPSConnection(base_url)
        conn.request("POST", f"/face/v1.0/persongroups/{group_id}/persons/{person_id}/persistedFaces?{params}",
                     body, headers_local)
        response = conn.getresponse()
        data = response.read()
        print(f"{file_name} 200 OK: {data}")
        conn.close()
    except Exception as e:
        print("[Errno {0}] {1}".format(e.errno, e.strerror))


@cli.command(help='Display all groups.')
def display_groups():
    params = urllib.parse.urlencode({
    })

    try:
        conn = http.client.HTTPSConnection(base_url)
        conn.request("GET", "/face/v1.0/persongroups?%s" % params, "", headers)
        response = conn.getresponse()
        data = response.read()
        print(f"200 OK: {data}")
        conn.close()
    except Exception as e:
        print("[Errno {0}] {1}".format(e.errno, e.strerror))


@cli.command(help='Delete a group by group id')
@click.option('--group_id', prompt='Group id', help='Group id')
def delete_group(group_id):
    params = urllib.parse.urlencode({
    })

    try:
        conn = http.client.HTTPSConnection(base_url)
        conn.request("DELETE", f"/face/v1.0/persongroups/{group_id}?{params}", "", headers)
        response = conn.getresponse()
        data = response.read()
        print(f"200 OK: {data}")
        conn.close()
    except Exception as e:
        print("[Errno {0}] {1}".format(e.errno, e.strerror))


@cli.command(help='Train group')
@click.option('--group_id', default='c_h', help='Unique group id')
def train_group(group_id):
    params = urllib.parse.urlencode({
    })

    try:
        conn = http.client.HTTPSConnection(base_url)
        conn.request("POST", f"/face/v1.0/persongroups/{group_id}/train?{params}", "", headers)
        response = conn.getresponse()
        data = response.read()
        print(f"200 OK: {data}")
        conn.close()
    except Exception as e:
        print("[Errno {0}] {1}".format(e.errno, e.strerror))


@cli.command(help='Check status on training of group')
@click.option('--group_id', default='c_h', help='Unique group id')
def group_train_status(group_id):
    params = urllib.parse.urlencode({
    })

    try:
        conn = http.client.HTTPSConnection(base_url)
        conn.request("GET", f"/face/v1.0/persongroups/{group_id}/training?{params}", "", headers)
        response = conn.getresponse()
        data = response.read()
        print(f"200 OK: {data}")
        conn.close()
    except Exception as e:
        print("[Errno {0}] {1}".format(e.errno, e.strerror))


@cli.command(help='List persons in group')
@click.option('--group_id', default='c_h', help='Unique group id')
def list_persons(group_id):
    params = urllib.parse.urlencode({
    })

    try:
        conn = http.client.HTTPSConnection(base_url)
        conn.request("GET", f"/face/v1.0/persongroups/{group_id}/persons?{params}", "{body}", headers)
        response = conn.getresponse()
        data = response.read()
        print(f"200 OK: {data}")
        conn.close()
    except Exception as e:
        print("[Errno {0}] {1}".format(e.errno, e.strerror))


@cli.command(help='Delete face associated with person')
@click.option('--group_id', default='c_h', help='Unique group id')
@click.option('--person_id', prompt='Person name', help='Unique person id, retrieve with list_persons')
@click.option('--persisted_face_id', prompt='Persisted face id', help='Unique face id')
def delete_face(group_id, person_id, persisted_face_id):
    params = urllib.parse.urlencode({
    })

    try:
        conn = http.client.HTTPSConnection(base_url)
        conn.request("DELETE",
                     f"/face/v1.0/persongroups/{group_id}/persons/{person_id}/persistedFaces/{persisted_face_id}?{params}",
                     "", headers)
        response = conn.getresponse()
        data = response.read()
        print(f"200 OK: {data}")
        conn.close()
    except Exception as e:
        print("[Errno {0}] {1}".format(e.errno, e.strerror))


@cli.command(help='Delete person by group id and person id')
@click.option('--group_id', default='c_h', help='Unique group id')
@click.option('--person_id', prompt='Person name', help='Unique person id')
def delete_person(group_id, person_id):
    params = urllib.parse.urlencode({
    })

    try:
        conn = http.client.HTTPSConnection(base_url)
        conn.request("DELETE", f"/face/v1.0/persongroups/{group_id}/persons/{person_id}?{params}", "",
                     headers)
        response = conn.getresponse()
        data = response.read()
        print(f"200 OK: {data}")
        conn.close()
    except Exception as e:
        print("[Errno {0}] {1}".format(e.errno, e.strerror))


@cli.command('Associate all pictures in directory with a person')
@click.option('--group_id', default='c_h', help='Unique group id')
@click.option('--person_id', prompt='Person name', help='Unique person id')
@click.option('--dir', prompt='JPG file directory', help='Directory')
def add_face_dir(group_id, person_id, dir):
    filelist = [f"{dir}/{file}" for file in os.listdir(dir) if file.endswith('.jpg')]
    for file in filelist:
        add_face(group_id, person_id, file)



# create_group("c_h2", "CambridgeHack")
# create_person("c_h", "Rolf")
# add_face("c_h", "c7e83600-b7ac-478f-a3eb-ebe716f9f4db", "20180120_151104.jpg")
# display_groups()
# delete_group("c_h2")
# train_group("c_h")
# group_train_status("c_h")
# list_persons("c_h")
# delete_face("c_h"t, "c7e83600-b7ac-478f-a3eb-ebe716f9f4db", "59a2bcb6-a106-4f3c-95e1-4aa5f1cb889a")
# delete_person("c_h", "c7e83600-b7ac-478f-a3eb-ebe716f9f4db")
# list_persons("c_h")
# add_face_dir("c_h", "df973ca6-b120-4bdd-8784-b3a47d0d1677", "Rolf")

if __name__ == '__main__':
    cli()