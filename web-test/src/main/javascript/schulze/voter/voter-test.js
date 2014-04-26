require(['jquery', 'underscore', '/schulze/voter/voter.js'],
    function ($, _, createVoterComponent) {
        'use strict';
        module('voter');
        function notVisible(element, message) {
            equal(element.css('display'), 'none', message);
        }

        function isVisible(element, message) {
            notEqual(element.css('display'), 'none', message);
        }

        function extractName(element) {
            return element.innerText;
        }

        test('initially display voter name and elections', function () {
            var serverApi, voterComponent, electionNamesCalled, voterNamesQueried;

            //given
            electionNamesCalled = 0;
            voterNamesQueried = [];
            serverApi = {
                interestedInElections: function () {
                    electionNamesCalled++;
                    voterComponent.notifyRegardingElections([{name:'Ice Cream'}, {name:'Programming Language'}, {name:'Restaurant'}]);
                },
                interestedInVoterExistence: function (voterName) {
                    voterNamesQueried.push(voterName);
                    voterComponent.notifyRegardingVoterExistence(true);
                }
            };

            voterComponent = createVoterComponent({url: '/voter?name=Alice', serverApi: serverApi});

            //when
            voterComponent.initialize();

            //then
            equal(electionNamesCalled, 1, 'asked for election names');
            equal(voterNamesQueried.length, 1, 'queried voter once');
            equal(voterNamesQueried[0], 'Alice', 'queried correct voter');

            isVisible(voterComponent.dom.find('.voter-exists'), 'voter exists section is visible');
            notVisible(voterComponent.dom.find('.voter-scheduled-for-deletion'), 'voter scheduled for deletion section is not visible');
            notVisible(voterComponent.dom.find('.voter-does-not-exist'), 'voter does not exist section is not visible');
            equal(voterComponent.dom.find('.voter-name').html(), 'Alice', 'voter name was pulled from url');
            deepEqual(voterComponent.dom.find('.election').get().map(extractName), ['Ice Cream', 'Programming Language', 'Restaurant'], 'got elections from server api');
        });
        test('initially display non existent voter', function () {
            var serverApi, voterComponent, voterNamesQueried;

            //given
            voterNamesQueried = [];
            serverApi = {
                interestedInElections: function () {
                },
                interestedInVoterExistence: function (voterName) {
                    voterNamesQueried.push(voterName);
                    voterComponent.notifyRegardingVoterExistence(false);
                }
            };

            voterComponent = createVoterComponent({url: '/voter?name=Bob', serverApi: serverApi});

            //when
            voterComponent.initialize()

            //then
            equal(voterNamesQueried.length, 1, 'queried voter once');
            equal(voterNamesQueried[0], 'Bob', 'queried correct voter');
            notVisible(voterComponent.dom.find('.voter-exists'), 'voter exists section is not visible');
            notVisible(voterComponent.dom.find('.voter-scheduled-for-deletion'), 'voter scheduled for deletion section is not visible');
            isVisible(voterComponent.dom.find('.voter-does-not-exist'), 'voter does not exist section is visible');
            equal(voterComponent.dom.find('.voter-name').html(), 'Bob', 'voter name was pulled from url');
        });
        test('schedule delete voter', function () {
            var serverApi, voterComponent, votersDeleted;

            //given
            votersDeleted = [];
            serverApi = {
                interestedInElections: function () {
                },
                interestedInVoterExistence: function (voterName) {
                    voterComponent.notifyRegardingVoterExistence(true);
                },
                deleteVoter: function (voterName) {
                    votersDeleted.push(voterName)
                }
            };

            voterComponent = createVoterComponent({url: '/voter?name=Carol', serverApi: serverApi});

            //when
            voterComponent.initialize();
            voterComponent.dom.find('.delete-voter').trigger('click');

            //then
            equal(votersDeleted.length, 1, 'exactly one voter was deleted');
            equal(votersDeleted[0], 'Carol', 'the correct voter was deleted');
            notVisible(voterComponent.dom.find('.voter-exists'), 'voter exists section is not visible');
            isVisible(voterComponent.dom.find('.voter-scheduled-for-deletion'), 'voter scheduled for deletion section is visible');
            notVisible(voterComponent.dom.find('.voter-does-not-exist'), 'voter does not exist section is not visible');
        });
        test('voter deleted', function () {
            var serverApi, voterComponent, votersDeleted;

            //given
            votersDeleted = [];
            serverApi = {
                interestedInElections: function () {
                },
                interestedInVoterExistence: function (voterName) {
                    voterComponent.notifyRegardingVoterExistence(true);
                },
                deleteVoter: function (voterName) {
                    votersDeleted.push(voterName)
                    voterComponent.notifyRegardingVoterExistence(false);
                }
            };

            voterComponent = createVoterComponent({url: '/voter?name=Carol', serverApi: serverApi});

            //when
            voterComponent.initialize();
            voterComponent.dom.find('.delete-voter').trigger('click');

            //then
            equal(votersDeleted.length, 1, 'exactly one voter was deleted');
            equal(votersDeleted[0], 'Carol', 'the correct voter was deleted');
            notVisible(voterComponent.dom.find('.voter-exists'), 'voter exists section is not visible');
            notVisible(voterComponent.dom.find('.voter-scheduled-for-deletion'), 'voter scheduled for deletion section is not visible');
            isVisible(voterComponent.dom.find('.voter-does-not-exist'), 'voter does not exist section is visible');
        });
    });
