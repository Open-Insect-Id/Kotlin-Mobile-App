# Kotlin Insect ID Mobile App

Kotlin Insect ID is a mobile application built as part of the [Trophées de la NSI](https://trophees-nsi.fr/) challenge to recognize insects using a custom AI model trained us. The app is designed primarily for learning and experimentation, and is distributed under the MIT license.

## Project goals

- Demonstrate how to integrate a **locally running AI model** (ONNX) into a modern Android app.
- Provide a **practical NSI project** that covers machine learning, mobile development, and UX.
- Offer a simple tool to **identify insects from photos** without requiring a constant internet connection.
- Easier to iterate than on than Kivy or Flet (python code, hard to develop a clean android app) for testing features and debugging.

## Main features

- **On‑device inference**  
  The app runs the insect classifier entirely on the device using our trained model.

- **Image input from camera or gallery**  
  Users can:
    - Take a picture with the camera.
    - Pick an existing picture from the device storage.

- **Insect recognition**  
  After selecting an image, the app:
    - Preprocesses the image for the model.
    - Runs inference locally.
    - Displays the predicted information (order, family, genre, specie) based on the model’s output.

- **Online image fetching (reference images)**  
  Once an insect is recognized, the app can:
    - Build a query from the predicted labels.
    - Fetch example images from external image APIs (such as Pixabay or Unsplash, depending on configuration).
    - Display a small gallery/grid of related images so the user can visually compare and validate the prediction.
    - User can click on each of these images to open them in browser and later download

- **History and debugging tools**  
  The app provides:
    - A simple history of past images.
    - Debug information to make it easier to understand model behaviour and troubleshoot issues during development.

## Architecture overview

- **Language & UI**: Kotlin with Jetpack Compose for modern declarative UI.
- **AI / Inference**: ONNX Runtime to load and run the custom insect model entirely on device.
- **Networking**: HTTP client to call image search APIs and parse JSON responses.
- **Image loading**: Coil to efficiently display both local images and remote thumbnails in the UI.
- **Storage**: Simple local storage abstraction for saving and retrieving the last analyzed image and optional history.

## Use cases

- NSI / educational project to:
    - Experiment with training image classification models for insects.
    - Learn how to deploy and use those models inside a native Android app.
    - Explore performance and UX trade‑offs between local processing and online resources.
    - Simply learn from insects

## License

This project is released under the **MIT License**
