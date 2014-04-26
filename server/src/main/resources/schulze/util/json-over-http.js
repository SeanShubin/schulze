define([], function () {
    'use strict';
    return function (options, callback) {
        var uri, method, body, client;
        uri = options.uri;
        method = options.method;
        body = JSON.stringify(options.body);

        function handler() {
            var callbackOptions;
            if (this.readyState == this.DONE) {
                if(this.response === '') {
                    callbackOptions = {
                        status: this.status
                    };
                } else {
                    callbackOptions = {
                        status: this.status,
                        body: JSON.parse(this.response)
                    };
                }
                callback(callbackOptions);
            }
        }

        client = new XMLHttpRequest();
        client.onreadystatechange = handler;
        client.open(method, uri);
        client.setRequestHeader('Content-Type', 'application/json');
        client.send(body);
    };
});
