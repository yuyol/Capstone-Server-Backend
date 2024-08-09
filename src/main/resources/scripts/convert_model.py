import tensorflow as tf
import sys

def convert_model(input_path, output_path):
    model = tf.keras.models.load_model(input_path)
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model = converter.convert()
    with open(output_path, 'wb') as f:
        f.write(tflite_model)

if __name__ == "__main__":
    input_path = sys.argv[1]
    output_path = sys.argv[2]
    convert_model(input_path, output_path)
