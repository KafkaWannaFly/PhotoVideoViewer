import numpy as np 
import cv2
from PIL import Image
import base64
import io
import face_recognition
# f = open("input.txt", "r")
# data1 = f.read()
allFace = []

def main(data1, data2):
	decoded_data1 = base64.b64decode(data1)
	np_data1 = np.fromstring(decoded_data1, np.uint8)
	img1 = cv2.imdecode(np_data1, cv2.IMREAD_UNCHANGED)
	img1out = cv2.cvtColor(img1, cv2.COLOR_BGR2RGB)

	decoded_data2 = base64.b64decode(data2)
	np_data2 = np.fromstring(decoded_data2, np.uint8)
	img2 = cv2.imdecode(np_data2, cv2.IMREAD_UNCHANGED)
	img2out = cv2.cvtColor(img2, cv2.COLOR_BGR2RGB)
	# img1out = convertData(data1)
	# img2out = convertData(data2)

	biden_encoding = face_recognition.face_encodings(img1out)[0]
	try:
		unknown_encoding = face_recognition.face_encodings(img2out)[0]
	except:
		return False
	results = face_recognition.compare_faces([biden_encoding], unknown_encoding)

	return results[0]
def main_test(data1):
	decoded_data1 = base64.b64decode(data1)
	np_data1 = np.fromstring(decoded_data1, np.uint8)
	img1 = cv2.imdecode(np_data1, cv2.IMREAD_UNCHANGED)
	img1out = cv2.cvtColor(img1, cv2.COLOR_BGR2RGB)
	img_gray = cv2.cvtColor(img1, cv2.COLOR_BGR2GRAY)

	face_locations = face_recognition.face_locations(img_gray)
	for (top, right, bottom, left) in face_locations:
		face_image = img1out[top:bottom, left:right]
		pil_im = Image.fromarray(face_image)
		buff = io.BytesIO()
		pil_im.save(buff, format="PNG")
		img_str = base64.b64encode(buff.getvalue())
		inputFace = str(img_str, 'UTF-8') + ""
		#inputFace = img_str
		if(len(allFace) == 0):
			allFace.append(inputFace)
		else:
			check = 0
			for face in allFace:
				try:
					if(main(face, inputFace) == 1):
						check = 1
						break
				except:
					return face + "*****" + inputFace
			if(check == 0):
				allFace.append(inputFace)
	return ""
	# return "" + str(img_str, 'UTF-8');
# def createDataPeopleExplore():

def get_data_face():
	return allFace	
# def main_test(data1):
# 	decoded_data1 = base64.b64decode(data1)
# 	np_data1 = np.fromstring(decoded_data1, np.uint8)
# 	img1 = cv2.imdecode(np_data1, cv2.IMREAD_UNCHANGED)
# 	imgout = cv2.cvtColor(img1, cv2.COLOR_BGR2RGB)	
# 	face_locations = face_recognition.face_locations(imgout)

# 	for face_location in face_locations:
# 		top, right, bottom, left = face_location
#     	face_image = imgout[top:bottom, left:right]
#     	pil_image = Image.fromarray(face_image)
#     	buff = io.BytesIO()
#     	pil_image.save(buff,format="JPG")
#     	img_str = base64.b64encode(buff.getvalue())
#     	return ""+str(img_str, 'utf-8')
#     return ""
#main_test(data1)