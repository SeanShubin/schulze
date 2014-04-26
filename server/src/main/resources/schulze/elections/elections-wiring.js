require([
    'jquery',
    '/schulze/api/server-api.js',
    '/schulze/elections/elections.js',
    '/schulze/util/json-over-http.js'],
    function ($, createServerApi, makeElectionsComponent, jsonOverHttp) {
        'use strict';
        var options, component, serverApi;
        serverApi = createServerApi(jsonOverHttp);
        options = {
            serverApi: serverApi
        };

        component = makeElectionsComponent(options);
        serverApi.setListener(component);
        $('body').append(component.dom);
        component.initialize();
    });
