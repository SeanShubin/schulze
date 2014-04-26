require(['jquery', 'underscore', '/schulze/elections/elections.js'],
    function ($, _, createElectionsComponent) {
        'use strict';
        module('elections');
        function triggerAddElectionUsingEnterKey(dom) {
            var event = $.Event('keypress', {keyCode: 13});
            dom.find('.election-name').trigger(event);
        }

        function triggerAddElectionUsingButton(dom) {
            dom.find('.add-election').trigger('click');
        }

        function addElectionUsingTrigger(triggerAddElection) {
            return function () {
                var serverApi, electionsComponent, addElectionCalls;

                addElectionCalls = [];
                serverApi = {
                    interestedInElections: function () {
                    },
                    addElection:function(electionName){
                        addElectionCalls.push(electionName);
                    }
                };
                electionsComponent = createElectionsComponent({serverApi: serverApi});
                electionsComponent.initialize();

                electionsComponent.dom.find('.election-name').val('Dave');
                triggerAddElection(electionsComponent.dom);
                equal(addElectionCalls.length, 1, 'one call to add election');
                equal(addElectionCalls[0], 'Dave', 'correct election was added')
            }
        }

        function extractName(element) {
            return element.innerText;
        }

        test('initially ask for existing elections', function () {
            var serverApi, electionsComponent, electionNamesCalled;

            //given
            electionNamesCalled = 0;
            serverApi = {
                interestedInElections: function () {
                    electionNamesCalled++;
                }
            };
            electionsComponent = createElectionsComponent({serverApi: serverApi});

            //when
            electionsComponent.initialize();

            //then
            equal(electionNamesCalled, 1, 'asked for election names')
        });

        test('display elections', function () {
            var serverApi, electionsComponent;

            //given
            serverApi = {
                interestedInElections: function () {
                }
            };
            electionsComponent = createElectionsComponent({serverApi: serverApi});
            electionsComponent.initialize();

            //when
            electionsComponent.notifyRegardingElections([{name:'Alice'}, {name:'Bob'}, {name:'Carol'}]);

            //then
            deepEqual(electionsComponent.dom.find('.election').get().map(extractName), ['Alice', 'Bob', 'Carol']);

        });

        test('add election using enter key', addElectionUsingTrigger(triggerAddElectionUsingEnterKey));
        test('add election using button', addElectionUsingTrigger(triggerAddElectionUsingButton));

        test('ignore blank elections', function () {
            var serverApi, electionsComponent, addElectionCalls;

            addElectionCalls = [];
            serverApi = {
                interestedInElections: function () {
                },
                addElection:function(electionName){
                    addElectionCalls.push(electionName);
                }
            };
            electionsComponent = createElectionsComponent({serverApi: serverApi});
            electionsComponent.initialize();

            electionsComponent.dom.find('.election-name').val('   ');
            electionsComponent.dom.find('.add-election').trigger('click');
            equal(addElectionCalls.length, 0, 'no calls to add election');
        });
    });
