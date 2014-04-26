require([
    'jquery',
    '/schulze/api/server-api.js',
    '/schulze/vote/vote.js',
    '/schulze/util/json-over-http.js'],
    function ($, createServerApi, createVoteComponent, jsonOverHttp) {
        'use strict';
        var options, component, serverApi;
        serverApi = createServerApi(jsonOverHttp);
        options = {
            serverApi:serverApi,
            url: location
        };
        component = createVoteComponent(options);
        serverApi.setListener(component);
        $('body').append(component.dom);
        component.initialize();
    });
