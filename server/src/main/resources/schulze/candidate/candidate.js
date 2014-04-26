define([], function () {
        'use strict';
        function candidatePage(options) {
            var view = options.view,
                sendRequest = options.sendRequest,
                electionName = options.electionName,
                candidateName = options.candidateName,
                candidateExistsSection = view.find('.candidate-exists-section'),
                candidateDoesNotExistSection = view.find('.candidate-does-not-exist-section'),
                candidateScheduledForDeletionSection = view.find('.candidate-scheduled-for-deletion-section'),
                loadingCandidateFromServerSection = view.find('.loading-candidate-from-server-section'),
                problemWithServer = view.find('.problem-with-server-section'),
                descriptionEditor = view.find('.candidate-description'),
                deleteCandidateButton = view.find('.delete-candidate'),
                saveDescriptionButton = view.find('.save-description');

            function isSuccess(status) {
                return status >= 200 && status < 400;
            }

            function hideAllSections() {
                candidateExistsSection.hide();
                candidateDoesNotExistSection.hide();
                candidateScheduledForDeletionSection.hide();
                loadingCandidateFromServerSection.hide();
                problemWithServer.hide();
            }

            function onDeleteCandidate(){
                hideAllSections();
                candidateScheduledForDeletionSection.show();
                sendRequest({
                        method: 'DELETE',
                        uri: '/elections/' + encodeURIComponent(electionName) + '/candidates/' + encodeURIComponent(candidateName)},
                    function (response) {
                        getCandidate();
                    });
            }

            function onBeginEditing(){
                saveDescriptionButton.show();
            }

            function isBlank(str) {
                return (!str || /^\s*$/.test(str));
            }

            function onEndEditing(){
                var body;
                if(isBlank(descriptionEditor.val())) {
                    body = {name: candidateName}
                } else {
                    body = {name: candidateName, description: descriptionEditor.val()}
                }
                sendRequest({
                        method: 'PATCH',
                        uri: '/elections/' + encodeURIComponent(electionName) + '/candidates/' + encodeURIComponent(candidateName),
                        body: body
                    },
                    function (response) {
                        if (isSuccess(response.status)) {
                            saveDescriptionButton.hide();
                        }
                    });
            }

            function setupEventHandling() {
                deleteCandidateButton.on('click', onDeleteCandidate);
                descriptionEditor.on('change', onBeginEditing);
                descriptionEditor.on('keydown', onBeginEditing);
                descriptionEditor.on('keypress', onBeginEditing);
                descriptionEditor.on('keyup', onBeginEditing);
                saveDescriptionButton.on('click', onEndEditing)
            }

            function updateCandidateDescription(description){
                descriptionEditor.val(description)
            }

            function getCandidate(){
                hideAllSections();
                loadingCandidateFromServerSection.show();
                sendRequest({
                        method: 'GET',
                        uri: '/elections/' + encodeURIComponent(electionName) + '/candidates/' + encodeURIComponent(candidateName)},
                    function (response) {
                        hideAllSections();
                        if (isSuccess(response.status)) {
                            candidateExistsSection.show();
                            updateCandidateDescription(response.body.description)
                        } else if (response.status == 404) {
                            candidateDoesNotExistSection.show();
                        } else {
                            problemWithServer.show();
                        }
                    });

            }

            function initialize() {
                view.find('.election-name').text(electionName)
                view.find('.candidate-name').text(candidateName)
                view.find('.election-page-link').attr('href', '../election/election.html?name=' + encodeURIComponent(electionName));
                saveDescriptionButton.hide();
                getCandidate();
                setupEventHandling();
            }

            initialize();
        }

        return candidatePage;
    });
