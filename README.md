# Net Tasker

Simple Android application to manage network requests.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

First of all you need to setup the gradle to be able to use the library.

Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```sh
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Step 2. Add the dependency
```sh
dependencies {
    compile 'com.github.iosiftalmacel:NetTasker:0.0.1'
}
```

### Examples

A series of examples of how to use the NetTask library.

Download photo into ImageView:
```
NetTasker.request(ImageDownload(imageView,{
    it.url = "www.example.com" 
    it.from = RequestFrom.Cache                         // should donwload from web or check if cached first
    it.errorDrawable = ColorDrawable(Color.RED)         // the drawable to show when there was an error downloading the photo
    it.placeholderDrawable = ColorDrawable(Color.GREY)  // the drawable to show until the photo is downloaded
    it.fadeDuration = 200                               // duration of the fade from the placeholder to the photo
    it.saveInMemory = true                              // should  save the photo in memory for faster access
    it.saveOnDisk = true                                // should  save the photo on the disk for faster access
    it.timeout = 8000                                   // the timeout of the request
}))
```

Download photo and listen for completion:
```
NetTasker.request(BitmapDownload(context,{
    it.url = "www.example.com"
    it.onComplete = {
        bitmap ->imageView.setImageBitmap(bitmap)  
    }
    it.onError = {
        Log.e("NetTask", "Error downloading the bitmap")
    }
}))
```

Download file:
```
NetTasker.request(FileDownload(context,{
    it.url = "www.example.com"
    it.fileName = "test.zip"
    it.path = context.cacheDir
    it.onComplete = {
        file -> Log.e("NetTask", "File size ${file.length() / 1024}")
    }
    it.onError = {
        Log.e("NetTask", "Error downloading file")
    }
}))
```

Download json:
```
NetTasker.request(JsonDownload(context, {
    it.url = "www.example.com"
    it.onCompleteObj = {
        jsonObj -> Log.e("NetTask", "Json download successful")
    }
    it.onError = {
        Log.e("NetTask", "Error downloading json")
    }
}))
```

Download Class Model:
```
class Example(val name: String, val age: Int)

NetTasker.request(ModelDownload(context, Example::class.java,{
    it.url = "www.example.com"
    it.onComplete = {
        model -> Log.e("NetTask", "Example: ${model.name}, ${model.age}")
    }
    it.onError = {
        Log.e("NetTask", "Error downloading json")
    }
}))
``` 

Upload file:
```
NetTasker.request(FileUpload(context, {
    it.url = "www.example.com"
    it.type = UploadType.POST
    it.filepath = "path/path/path.extension"
    it.onFinish = {
        wasSuccess -> Log.e("NetTask", "File was uploaded: $wasSuccess")
    }
}))
```

## Built With

* Android - Was developed for android applications
* Kotlin - Was developed in Kotlin
* Gson Library - Used to transfom json to gson

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
