/// <reference path="d.ts/DefinitelyTyped/jquery/jquery.d.ts" />
/// <reference path="d.ts/DefinitelyTyped/knockout/knockout.d.ts" />

var viewModel: ViewModel = null
var xhr: XMLHttpRequest

$(() => {
    viewModel = new ViewModel()
    ko.applyBindings(viewModel)
})

class ViewModel {
    
    progressIndicatorWidth: KnockoutObservableString;
    isProgressBarVisible: KnockoutObservableBool;
    isSuccessAlertVisible: KnockoutObservableBool;
    isFailureAlertVisible: KnockoutObservableBool;
    fileInputValue: KnockoutObservableString;
    service: UploadService; 

    constructor() {
        this.progressIndicatorWidth = ko.observable("0")
        this.isProgressBarVisible = ko.observable(false)
        this.isSuccessAlertVisible = ko.observable(false)
        this.isFailureAlertVisible = ko.observable(false)
        this.fileInputValue = ko.observable("")
        this.service = new UploadService("http://localhost:8080/")
    }

    fileInputChangeHandler(data, event) {
        var files = event.target.files
        this.executeUpload(files[0])
    }
        
    executeUpload(file: File) {
        this.service.upload(file, () => {
            console.log("upload complete.")
            this.isProgressBarVisible(false)
            this.isSuccessAlertVisible(true)
            this.clearFileInputValue()
        },
        errorMessage => {
            console.log("upload failed. " + errorMessage)    
            this.isProgressBarVisible(false)
            this.isFailureAlertVisible(true)
            this.clearFileInputValue()
        },
        progress => {
            var percent = progress * 100
            this.progressIndicatorWidth(percent + "%")
        })
        
        this.hideAllAlert()
        this.resetProgressBar()
        this.isProgressBarVisible(true)
    }
    
    hideAllAlert() {
        this.isSuccessAlertVisible(false)
        this.isFailureAlertVisible(false)
    }
    
    clearFileInputValue() {
        this.fileInputValue("")
    }
    
    resetProgressBar() {
        this.progressIndicatorWidth("0")
    }
}

class UploadService {
    
    constructor(public hostUrl: string) {
    }
        
    upload(file: File, resultHandler: () => void, 
        errorHandler: (errorMessage: string) => void,
        progressHandler: (progress: number) => void) {
        $.ajax({
            url: this.hostUrl + "sign/put",
            type: "GET",
            data: { "name": file.name, "type": file.type }
        })
        .then(data => {  
            console.log("Start upload file.")
            
            var deferred = $.Deferred()
            var xhr = this.createCORSRequest("PUT", decodeURIComponent(data["url"]))
            xhr.onload = e => deferred.resolve(e)
            xhr.onerror = e => deferred.reject(e)
            xhr.upload.onprogress = e => deferred.notify(e)
            xhr.send(file)
            
            return deferred
        })
        .progress(e => {
            var progressValue = 0.0
            if (e.lengthComputable) {
                progressValue = e.loaded / e.total
            }
            progressHandler(progressValue)
        })
        .done(e => resultHandler()) 
        .fail((jqHXR, textStatus, errorThrow) => errorHandler(errorThrow))
    }
    
    createCORSRequest(method: string, url: string) {
        var xhr: any = new XMLHttpRequest();
        if ("withCredentials" in xhr) {
            xhr.open(method, url, true);
        } else if (typeof XDomainRequest != "undefined") {
            xhr = new XDomainRequest();
            xhr.open(method, url);
        } else {
            xhr = null;
        }
        return xhr;
    }
}