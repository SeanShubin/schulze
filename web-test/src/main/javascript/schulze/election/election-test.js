require(['jquery', 'underscore', '/schulze/election/election.js'],
    function ($, _, createElectionComponent) {
        'use strict';
        module('election');
        function notVisible(element, message) {
            equal(element.css('display'), 'none', message);
        }

        function isVisible(element, message) {
            notEqual(element.css('display'), 'none', message);
        }

        function extractName(element) {
            return element.innerText;
        }

        test('initially display election name, candidates, and voters', function () {
            var serverApi, electionComponent, candidateNamesCalled, voterNamesCalled, electionNamesQueried;

            //given
            candidateNamesCalled = 0;
            voterNamesCalled = 0;
            electionNamesQueried = [];
            serverApi = {
                interestedInCandidates: function () {
                    candidateNamesCalled++;
                    electionComponent.notifyRegardingCandidates([
                        {name: 'Haskell'},
                        {name: 'Scala'},
                        {name: 'Clojure'}
                    ]);
                },
                interestedInVoters: function () {
                    voterNamesCalled++;
                    electionComponent.notifyRegardingVoters([
                        {name: 'Alice'},
                        {name: 'Bob'},
                        {name: 'Carol'}
                    ]);
                },
                interestedInElectionExistence: function (electionName) {
                    electionNamesQueried.push(electionName);
                    electionComponent.notifyRegardingElectionExistence(true);
                }
            };

            electionComponent = createElectionComponent({url: '/election?name=' + encodeURIComponent('Programming Languages'), serverApi: serverApi});

            //when
            electionComponent.initialize();

            //then
            equal(candidateNamesCalled, 1, 'asked for candidate names');
            equal(voterNamesCalled, 1, 'asked for voter names');

            equal(electionNamesQueried.length, 1, 'queried election once');
            equal(electionNamesQueried[0], 'Programming Languages', 'queried correct election');

            isVisible(electionComponent.dom.find('.election-exists'), 'election exists section is visible');
            notVisible(electionComponent.dom.find('.election-scheduled-for-deletion'), 'election scheduled for deletion section is not visible');
            notVisible(electionComponent.dom.find('.election-does-not-exist'), 'election does not exist section is not visible');
            equal(electionComponent.dom.find('.election-name').html(), 'Programming Languages', 'election name was pulled from url');
            deepEqual(electionComponent.dom.find('.candidate').get().map(extractName), ['Haskell', 'Scala', 'Clojure'], 'got candidates from server api');
            deepEqual(electionComponent.dom.find('.voter').get().map(extractName), ['Alice', 'Bob', 'Carol'], 'got voters from server api');
        });
        test('initially display non existent election', function () {
            var serverApi, electionComponent, electionNamesQueried;

            //given
            electionNamesQueried = [];
            serverApi = {
                interestedInCandidates: function () {
                },
                interestedInVoters: function () {
                },
                interestedInElectionExistence: function (electionName) {
                    electionNamesQueried.push(electionName);
                    electionComponent.notifyRegardingElectionExistence(false);
                }
            };

            electionComponent = createElectionComponent({url: '/election?name=' + encodeURIComponent('Programming Languages'), serverApi: serverApi});

            //when
            electionComponent.initialize();

            //then
            equal(electionNamesQueried.length, 1, 'queried election once');
            equal(electionNamesQueried[0], 'Programming Languages', 'queried correct election');
            notVisible(electionComponent.dom.find('.election-exists'), 'election exists section is not visible');
            notVisible(electionComponent.dom.find('.election-scheduled-for-deletion'), 'election scheduled for deletion section is not visible');
            isVisible(electionComponent.dom.find('.election-does-not-exist'), 'election does not exist section is visible');
            equal(electionComponent.dom.find('.election-name').html(), 'Programming Languages', 'election name was pulled from url');
        });
        test('schedule delete election', function () {
            var serverApi, electionComponent, electionsDeleted;

            //given
            electionsDeleted = [];
            serverApi = {
                interestedInCandidates: function () {
                },
                interestedInVoters: function () {
                },
                interestedInElectionExistence: function () {
                    electionComponent.notifyRegardingElectionExistence(true);
                },
                deleteElection: function (electionName) {
                    electionsDeleted.push(electionName)
                }
            };

            electionComponent = createElectionComponent({url: '/election?name=' + encodeURIComponent('Programming Languages'), serverApi: serverApi});

            //when
            electionComponent.initialize();
            electionComponent.dom.find('.delete-election').trigger('click');

            //then
            equal(electionsDeleted.length, 1, 'exactly one election was deleted');
            equal(electionsDeleted[0], 'Programming Languages', 'the correct election was deleted');
            notVisible(electionComponent.dom.find('.election-exists'), 'election exists section is not visible');
            isVisible(electionComponent.dom.find('.election-scheduled-for-deletion'), 'election scheduled for deletion section is visible');
            notVisible(electionComponent.dom.find('.election-does-not-exist'), 'election does not exist section is not visible');
        });
        test('election deleted', function () {
            var serverApi, electionComponent, electionsDeleted;

            //given
            electionsDeleted = [];
            serverApi = {
                interestedInCandidates: function () {
                },
                interestedInVoters: function () {
                },
                interestedInElectionExistence: function () {
                    electionComponent.notifyRegardingElectionExistence(true);
                },
                deleteElection: function (electionName) {
                    electionsDeleted.push(electionName)
                    electionComponent.notifyRegardingElectionExistence(false);
                }
            };

            electionComponent = createElectionComponent({url: '/election?name=' + encodeURIComponent('Programming Languages'), serverApi: serverApi});

            //when
            electionComponent.initialize();
            electionComponent.dom.find('.delete-election').trigger('click');

            //then
            equal(electionsDeleted.length, 1, 'exactly one election was deleted');
            equal(electionsDeleted[0], 'Programming Languages', 'the correct election was deleted');
            notVisible(electionComponent.dom.find('.election-exists'), 'election exists section is not visible');
            notVisible(electionComponent.dom.find('.election-scheduled-for-deletion'), 'election scheduled for deletion section is not visible');
            isVisible(electionComponent.dom.find('.election-does-not-exist'), 'election does not exist section is visible');
        });

        function triggerAddCandidateUsingEnterKey(dom) {
            var event = $.Event('keypress', {keyCode: 13});
            dom.find('.candidate-name').trigger(event);
        }

        function triggerAddCandidateUsingButton(dom) {
            dom.find('.add-candidate').trigger('click');
        }

        function addCandidateUsingTrigger(triggerAddCandidate) {
            return function () {
                var serverApi, electionComponent, addCandidateCalls;

                addCandidateCalls = [];
                serverApi = {
                    interestedInCandidates: function () {
                    },
                    interestedInVoters: function () {
                    },
                    interestedInElectionExistence: function () {
                    },
                    addCandidate: function (options) {
                        addCandidateCalls.push(options);
                    }
                };
                electionComponent = createElectionComponent({url: '/election?name=' + encodeURIComponent('Programming Languages'), serverApi: serverApi});
                electionComponent.initialize();

                electionComponent.dom.find('.candidate-name').val('Haskell');
                triggerAddCandidate(electionComponent.dom);
                equal(addCandidateCalls.length, 1, 'one call to add candidate');
                deepEqual(addCandidateCalls[0], {electionName: 'Programming Languages', candidateName: 'Haskell'}, 'correct candidate was added')
            }
        }

        test('add candidate using enter key', addCandidateUsingTrigger(triggerAddCandidateUsingEnterKey));
        test('add candidate using button', addCandidateUsingTrigger(triggerAddCandidateUsingButton));
    });
