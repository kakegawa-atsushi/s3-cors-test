var viewModel = null;
var xhr;
$(function () {
    viewModel = new ViewModel();
    ko.applyBindings(viewModel);
});
var ViewModel = (function () {
    function ViewModel() {
        this.progressIndicatorWidth = ko.observable("0");
        this.isProgressBarVisible = ko.observable(false);
        this.isSuccessAlertVisible = ko.observable(false);
        this.isFailureAlertVisible = ko.observable(false);
        this.fileInputValue = ko.observable("");
        this.service = new UploadService("http://localhost:8080/");
    }
    ViewModel.prototype.fileInputChangeHandler = function (data, event) {
        var files = event.target.files;
        this.executeUpload(files[0]);
    };
    ViewModel.prototype.executeUpload = function (file) {
        var _this = this;
        this.service.upload(file, function () {
            console.log("upload complete.");
            _this.isProgressBarVisible(false);
            _this.isSuccessAlertVisible(true);
            _this.clearFileInputValue();
        }, function (errorMessage) {
            console.log("upload failed. " + errorMessage);
            _this.isProgressBarVisible(false);
            _this.isFailureAlertVisible(true);
            _this.clearFileInputValue();
        }, function (progress) {
            var percent = progress * 100;
            _this.progressIndicatorWidth(percent + "%");
        });
        this.hideAllAlert();
        this.resetProgressBar();
        this.isProgressBarVisible(true);
    };
    ViewModel.prototype.hideAllAlert = function () {
        this.isSuccessAlertVisible(false);
        this.isFailureAlertVisible(false);
    };
    ViewModel.prototype.clearFileInputValue = function () {
        this.fileInputValue("");
    };
    ViewModel.prototype.resetProgressBar = function () {
        this.progressIndicatorWidth("0");
    };
    return ViewModel;
})();
var UploadService = (function () {
    function UploadService(hostUrl) {
        this.hostUrl = hostUrl;
    }
    UploadService.prototype.upload = function (file, resultHandler, errorHandler, progressHandler) {
        var _this = this;
        $.ajax({
            url: this.hostUrl + "sign/put",
            type: "GET",
            data: {
                "name": file.name,
                "type": file.type
            }
        }).then(function (data) {
            console.log("Start upload file.");
            var deferred = $.Deferred();
            var xhr = _this.createCORSRequest("PUT", decodeURIComponent(data["url"]));
            xhr.onload = function (e) {
                return deferred.resolve(e);
            };
            xhr.onerror = function (e) {
                return deferred.reject(e);
            };
            xhr.upload.onprogress = function (e) {
                return deferred.notify(e);
            };
            xhr.send(file);
            return deferred;
        }).progress(function (e) {
            var progressValue = 0.0;
            if(e.lengthComputable) {
                progressValue = e.loaded / e.total;
            }
            progressHandler(progressValue);
        }).done(function (e) {
            return resultHandler();
        }).fail(function (jqHXR, textStatus, errorThrow) {
            return errorHandler(errorThrow);
        });
    };
    UploadService.prototype.createCORSRequest = function (method, url) {
        var xhr = new XMLHttpRequest();
        if("withCredentials" in xhr) {
            xhr.open(method, url, true);
        } else if(typeof XDomainRequest != "undefined") {
            xhr = new XDomainRequest();
            xhr.open(method, url);
        } else {
            xhr = null;
        }
        return xhr;
    };
    return UploadService;
})();
