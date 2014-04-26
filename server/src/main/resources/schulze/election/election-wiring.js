require([
    'jquery',
    '/schulze/api/server-api.js',
    '/schulze/election/election.js',
    '/schulze/util/json-over-http.js'],
    function ($, createServerApi, makeElectionComponent, jsonOverHttp) {
        'use strict';
        var options, component, serverApi;
        serverApi = createServerApi(jsonOverHttp);
        options = {
            serverApi:serverApi,
            url: location
        };
        component = makeElectionComponent(options);
        serverApi.setListener(component);
        $('body').append(component.dom);
        component.initialize();
    });
