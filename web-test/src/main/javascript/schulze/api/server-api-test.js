require(['jquery', 'underscore', '/schulze/api/server-api.js'],
    function ($, _, serverApiFactory) {
        'use strict';
        module('server-api');
        test('add election', function () {
            var serverApi, fakeJsonOverHttp, actualHttpCalls, fakeHttpCallbackCalls, fakeListener, actualListenerCalls;
            actualHttpCalls = [];
            actualListenerCalls = [];
            fakeHttpCallbackCalls = [
                {status: 200},
                {status: 200, body: [
                    {name: 'Ice Cream'}
                ]}
            ];
            fakeJsonOverHttp = function (options, callback) {
                actualHttpCalls.push(options);
                callback(fakeHttpCallbackCalls.shift());
            };
            fakeListener = {
                notifyRegardingElections: function (electionNames) {
                    actualListenerCalls.push(electionNames);
                }
            };
            serverApi = serverApiFactory(fakeJsonOverHttp);
            serverApi.setListener(fakeListener);
            serverApi.addElection('Ice Cream');

            equal(actualHttpCalls.length, 2, '');
            deepEqual({uri: "/elections", method: "POST", body: {name: 'Ice Cream'}}, actualHttpCalls[0], '');
            deepEqual({uri: "/elections", method: "GET"}, actualHttpCalls[1], '');

            equal(actualListenerCalls.length, 1, '');
            deepEqual(actualListenerCalls[0], [{name:'Ice Cream'}]);
        });

        test('interested in elections', function () {
            var serverApi, fakeJsonOverHttp, actualHttpCalls, fakeHttpCallbackCalls, fakeListener, actualListenerCalls;
            actualHttpCalls = [];
            actualListenerCalls = [];
            fakeHttpCallbackCalls = [
                {status: 200, body: [
                    {name: 'Ice Cream'},
                    {name: 'Movie'}
                ]}
            ];
            fakeJsonOverHttp = function (options, callback) {
                actualHttpCalls.push(options);
                callback(fakeHttpCallbackCalls.shift());
            };
            fakeListener = {
                notifyRegardingElections: function (electionNames) {
                    actualListenerCalls.push(electionNames);
                }
            };
            serverApi = serverApiFactory(fakeJsonOverHttp);
            serverApi.setListener(fakeListener);
            serverApi.interestedInElections();

            equal(actualHttpCalls.length, 1, '');
            deepEqual({uri: "/elections", method: "GET"}, actualHttpCalls[0], '');

            equal(actualListenerCalls.length, 1, '');
            deepEqual(actualListenerCalls[0], [{name:'Ice Cream'}, {name:'Movie'}]);
        });

//        test('interested in voter exists', function(){});
//        test('delete voter', function(){});
//        test('interested in election exists', function(){});
//        test('delete election', function(){});
//        test('interested in voter names', function(){});
//        test('add voter', function(){});
//        test('interested in candidate names', function(){});
//        test('add candidate', function(){});
    });
