# Copyright (C) 2009 The Android Open Source Project
# Copyright (c) 2011, The Linux Foundation. All rights reserved.
# Copyright (C) 2017-2018 The LineageOS Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import common

def LoadFilesMap(zip):
  try:
    data = zip.read("RADIO/filesmap")
  except KeyError:
    print("Warning: could not find RADIO/filesmap in %s." % zip)
    data = ""
  d = {}
  for line in data.splitlines():
    line = line.strip()
    if not line or line.startswith("#"): continue
    pieces = line.split()
    if not (len(pieces) == 2 or len(pieces) == 3):
      raise ValueError("malformed filesmap line: \"%s\"" % (line,))
    file_size = zip.getinfo("RADIO/" + pieces[0]).file_size
    d[pieces[0]] = (pieces[1], None, file_size)
  return d

def GetRadioFiles(z):
  out = {}
  for info in z.infolist():
    if info.filename.startswith("RADIO/") and (len(info.filename) > len("RADIO/")):
      fn = "RADIO/" + info.filename[6:]
      out[fn] = fn
  return out

def InstallRawImage(image_data, api_version, input_zip, fn, info, filesmap):
  # fn is in RADIO/* format. Extracting just file name.
  filename = fn[6:]
  if api_version >= 3:
    if filename not in filesmap:
      return
    partition = filesmap[filename][0]
    info.script.AppendExtra('package_extract_file("%s", "%s");' % ("firmware-update/" + filename, partition))
    common.ZipWriteStr(info.output_zip, "firmware-update/" + filename, image_data)
    return
  else:
    print("warning radio-update: no support for api_version less than 3.")

def InstallRadioFiles(info):
  files = GetRadioFiles(info.input_zip)
  if files == {}:
    print("warning radio-update: no radio image in input target_files; not flashing radio")
    return
  info.script.Print("Patching firmware images...")
  # Load filesmap file
  filesmap = LoadFilesMap(info.input_zip)
  if filesmap == {}:
    print("warning radio-update: no or invalid filesmap file found. not flashing radio")
    return
  for f in files:
    image_data = info.input_zip.read(f)
    InstallRawImage(image_data, info.input_version, info.input_zip, f, info, filesmap)
  return

def FullOTA_InstallEnd(info):
  OTA_InstallEnd(info)
  InstallRadioFiles(info)
  return

def IncrementalOTA_InstallEnd(info):
  OTA_InstallEnd(info)
  InstallRadioFiles(info)
  return

def AddImage(info, basename, dest):
  path = "IMAGES/" + basename
  if path not in info.input_zip.namelist():
    return

  data = info.input_zip.read(path)
  common.ZipWriteStr(info.output_zip, basename, data)
  info.script.Print("Patching {} image unconditionally...".format(dest.split('/')[-1]))
  info.script.AppendExtra('package_extract_file("%s", "%s");' % (basename, dest))

def OTA_InstallEnd(info):
  AddImage(info, "dtbo.img", "/dev/block/bootdevice/by-name/dtbo")
  AddImage(info, "vbmeta.img", "/dev/block/bootdevice/by-name/vbmeta")
  return
