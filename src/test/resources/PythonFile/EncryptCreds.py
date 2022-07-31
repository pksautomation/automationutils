import calendar
import copy
import math
import random
from datetime import datetime
import json


def encrypt_value(decrypted_value):
  random_number = math.floor(random.uniform(0, 1) * 89) + 10
  x = []
  for each_char in decrypted_value:
    char_to_ascii = ord(each_char)
    salted_ascii = char_to_ascii + int(random_number)
    ascii_to_hex = hex(salted_ascii)[2:]
    x.append(ascii_to_hex)
  x.append(int(random_number))
  final_value = ':'.join(str(y) for y in x)
  return final_value

def decrypt_value(encrypted_value):
  parts = encrypted_value.split(':')
  salt = parts.pop()
  final_value = ''
  for each_part in parts:
    salted_ascii = int(each_part, 16)
    actual_ascii = int(salted_ascii) - int(salt)
    original_char = chr(actual_ascii)
    final_value += original_char
  return final_value

def encrypt_json(decrypted_json):
  final_json = {}
  for each_key in decrypted_json:
    final_json.update({
        encrypt_value(each_key): encrypt_value(decrypted_json[each_key])
    })
  return final_json


def encrypt_object(decrypted_json):
  print("Got this json to encrypt", decrypted_json)
  semi_list = []
  final_dict = {}
  for each_key in decrypted_json:
    if isinstance(decrypted_json[each_key], list):
      for each_value in decrypted_json[each_key]:
        semi_data = {}
        for semi_key in each_value:
          semi_data[encrypt_value(semi_key)] = encrypt_value(
              each_value[semi_key])
        semi_list.append(semi_data)
        final_dict[encrypt_value(each_key)] = semi_list
      return final_dict
  return final_dict


def to_java_encode_json(realjson):
    final_dictionary = json.loads(realjson)
    enc_result = json.dumps(encrypt_json(final_dictionary))
    return enc_result

def to_java_decode_string(decodestring):
    dec_result = decrypt_value(decodestring)
    return dec_result