require([
    'jquery',
    '/schulze/api/server-api.js',
    '/schulze/voter/voter.js',
    '/schulze/util/json-over-http.js'],
    function ($, createServerApi, makeVoterComponent, jsonOverHttp) {
        'use strict';
        var options, component, serverApi;
        serverApi = createServerApi(jsonOverHttp);
        options = {
            serverApi:serverApi,
            url: location
        };
        component = makeVoterComponent(options);
        serverApi.setListener(component);
        $('body').append(component.dom);
        component.initialize();
    });
