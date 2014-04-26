require(['jquery', 'underscore', '/schulze/voters/voters.js'],
    function ($, _, createVotersComponent) {
        'use strict';
        module('voters');
        function triggerAddVoterUsingEnterKey(dom) {
            var event = $.Event('keypress', {keyCode: 13});
            dom.find('.voter-name').trigger(event);
        }

        function triggerAddVoterUsingButton(dom) {
            dom.find('.add-voter').trigger('click');
        }

        function addVoterUsingTrigger(triggerAddVoter) {
            return function () {
                var serverApi, votersComponent, addVoterCalls;

                addVoterCalls = [];
                serverApi = {
                    interestedInVoters: function () {
                    },
                    addVoter: function (voterName) {
                        addVoterCalls.push(voterName);
                    }
                };
                votersComponent = createVotersComponent({serverApi: serverApi});
                votersComponent.initialize();

                votersComponent.dom.find('.voter-name').val('Dave');
                triggerAddVoter(votersComponent.dom);
                equal(addVoterCalls.length, 1, 'one call to add voter');
                equal(addVoterCalls[0], 'Dave', 'correct voter was added')
            }
        }

        function extractName(element) {
            return element.innerText;
        }

        test('initially ask for existing voters', function () {
            var serverApi, votersComponent, voterNamesCalled;

            //given
            voterNamesCalled = 0;
            serverApi = {
                interestedInVoters: function () {
                    voterNamesCalled++;
                }
            };
            votersComponent = createVotersComponent({serverApi: serverApi});

            //when
            votersComponent.initialize();

            //then
            equal(voterNamesCalled, 1, 'asked for voter names')
        });

        test('display voters', function () {
            var serverApi, votersComponent;

            //given
            serverApi = {
                interestedInVoters: function () {
                }
            };
            votersComponent = createVotersComponent({serverApi: serverApi});
            votersComponent.initialize();

            //when
            votersComponent.notifyRegardingVoters([
                {name: 'Alice'},
                {name: 'Bob'},
                {name: 'Carol'}
            ]);

            //then
            deepEqual(votersComponent.dom.find('.voter').get().map(extractName), [
                'Alice',
                'Bob',
                'Carol'
            ]);

        });

        test('add voter using enter key', addVoterUsingTrigger(triggerAddVoterUsingEnterKey));
        test('add voter using button', addVoterUsingTrigger(triggerAddVoterUsingButton));

        test('ignore blank voters', function () {
            var serverApi, votersComponent, addVoterCalls;

            addVoterCalls = [];
            serverApi = {
                interestedInVoters: function () {
                },
                addVoter: function (voterName) {
                    addVoterCalls.push(voterName);
                }
            };
            votersComponent = createVotersComponent({serverApi: serverApi});
            votersComponent.initialize();

            votersComponent.dom.find('.voter-name').val('   ');
            votersComponent.dom.find('.add-voter').trigger('click');
            equal(addVoterCalls.length, 0, 'no calls to add voter');
        });
    });
