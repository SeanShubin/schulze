require(['jquery', 'underscore', '/schulze/util/json-over-http.js'], function ($, _, jsonOverHttp) {
    module('json over http');
    asyncTest('json over http', function () {
        'use strict';
        jsonOverHttp({uri: '/test', method: 'post', body: {name: 'foo', data: ['aaa', 'bbb', 'ccc']}}, function (response) {
            var id;
            equal(200, response.status, 'success response when creating test object');
            id = response.body
            jsonOverHttp({uri: '/test/' + id, method: 'get'}, function (response2) {
                deepEqual({id: id, name: 'foo', data: ['aaa', 'bbb', 'ccc']}, response2.body, 'collection has test object');
                start();
            });
        });
    });
});
