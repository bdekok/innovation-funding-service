*** Settings ***
Documentation     INFUND-901: As a lead applicant I want to invite application contributors to collaborate with me on the application, so that they can contribute to the application in a collaborative competition
...
...
...               INFUND-896: As a lead applicant i want to invite partner organisations to collaborate on line in my application, so that i can create the consortium needed to complete the proposed project
...
...
...               INFUND-928: As a lead applicant i want a separate screen within the application form, so that i can invite/track partners/contributors throughout the application process
...
...
...               INFUND-929: As a lead applicant i want to be able to have a separate screen, so that i can invite contributors to the application
...
...
...               INFUND-1463: As a user with an invitation to collaborate on an application but not registered with IFS I want to be able to confirm my organisation so that I only have to create my account to work on the application
Suite Teardown    TestTeardown User closes the browser
Force Tags        Email
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${INVITE_COLLABORATORS_PAGE}    ${SERVER}/application/1/contributors/invite?newApplication
${APPLICATION_TEAM_PAGE}    ${SERVER}/application/1/contributors

*** Test Cases ***
Lead applicant can access the Application team page
    [Documentation]    INFUND-928
    [Tags]    HappyPath
    [Setup]    Guest user log-in    &{lead_applicant_credentials}
    Given the user navigates to the page    ${APPLICATION_OVERVIEW_URL}
    And the user should see the text in the page    View team members and add collaborators
    When the user clicks the button/link    link=View team members and add collaborators
    Then the user should see the text in the page    Application team
    And the user should see the text in the page    View and manage your contributors and partners in the application.
    And the lead applicant should have the correct status

Valid invitation submit
    [Documentation]    INFUND-901
    [Tags]    HappyPath
    [Setup]    Delete the emails from the test mailbox
    Given the user navigates to the page    ${INVITE_COLLABORATORS_PAGE}
    When the applicant enters valid inputs
    Then the user should see the text in the page    Application team
    And the user should see the text in the page    Invites sent

Pending users are visible in the assign list but not clickable
    [Documentation]    INFUND-928
    ...
    ...    INFUND-1962
    [Tags]
    Given the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    Then the applicant cannot assign to pending invitees
    And the user should see the text in the page    Adrian Booth (pending)
    And the user navigates to the page    ${LOG_OUT}

Business organisation (accept invitation)
    [Documentation]    INFUND-1005
    ...    INFUND-2286
    ...    INFUND-1779
    ...    INFUND-2336
    [Tags]    HappyPath    Email
    When the user opens the mailbox and accepts the invitation to collaborate
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And user selects the radio button    organisationType    1
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user enters text to a text field    id=organisationSearchName    Nomensa
    And the user clicks the button/link    id=org-search
    And the user clicks the button/link    link=NOMENSA LTD
    And the user selects the same address
    And the user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user fills the create account form    Adrian    Booth
    And the user opens the mailbox and verifies the email from
    And the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}

User who accepted the invite should be able to log-in
    [Tags]
    Given the user clicks the button/link    jQuery=.button:contains("Log in")
    When guest user log-in    worth.email.test+inviteorg1@gmail.com    Passw0rd123
    Then the user should be redirected to the correct page    ${DASHBOARD_URL}
    And the user can see the updated company name throughout the application

User who accepted the invite can invite others to their own organisation
    [Documentation]    INFUND-2335
    [Tags]    Pending
    # pending as not yet finished
    When the user navigates to the page    ${MANAGE_CONTRIBUTORS_URL}
    Then the user should see the element    foobar

User who accepted the invite cannot invite others to other organisations
    [Documentation]    INFUND-2335
    [Tags]    Pending
    # pending as not yet finished
    When the user navigates to the page    ${MANAGE_CONTRIBUTORS_URL}
    Then the user should not see the element    foobar

Collaborator can change the name of their company and this updates throughout the application
    [Documentation]    INFUND-2083
    [Tags]    Pending
    # note - only pending because it isn't working yet!
    Given the lead applicant logs out
    And the invited user verifies their email
    When the user changes their company name
    Then the new company name should be shown throughout the application
    And the lead applicant logs back in
    And the new company name should be shown throughout the application

The collaborator who accepted the invite should be visible in the assign list
    [Documentation]    INFUND-1779
    [Tags]    HappyPath
    [Setup]    Run keywords    User closes the browser
    ...    AND    Log in as user    &{lead_applicant_credentials}
    When the user navigates to the page    ${PROJECT_SUMMARY_URL}
    And the user clicks the button/link    css=.assign-button
    Then the user should see the element    jQuery=button:contains("Adrian Booth")

Status of the invited people (Application team page)
    [Documentation]    INFUND-929
    [Tags]    HappyPath
    Given the user navigates to the page    ${APPLICATION_TEAM_PAGE}
    Then the status of the invited people should be correct in the application team page

Status of the invited people (Manage contributors page)
    [Documentation]    INFUND-928
    [Tags]    HappyPath
    Given the user navigates to the page    ${APPLICATION_TEAM_URL}
    When the user clicks the button/link    jQuery=.button:contains("Invite new contributors")
    Then the user should see the text in the page    Manage Contributors
    And the status of the people should be correct in the Manage contributors page

Invited collaborators are not editable
    [Documentation]    INFUND-929
    [Tags]
    Given the user navigates to the page    ${APPLICATION_TEAM_URL}
    When the user clicks the button/link    jQuery=.button:contains("Invite new contributors")
    Then the user should see the text in the page    Manage Contributors
    And the invited collaborators are not editable

The Lead applicant invites a non registered user in the same organisation
    [Documentation]    INFUND-928
    ...
    ...    INFUND-1463
    ...
    ...    This test checks if the invited partner who are in the same organisation they can go directly to the create account and they don't have to create an organisation first.
    [Tags]    HappyPath
    [Setup]    Delete the emails from the test mailbox
    Given the user navigates to the page    ${APPLICATION_TEAM_URL}
    When the user clicks the button/link    jQuery=.button:contains("Invite new contributors")
    Then the user should see the text in the page    Manage Contributors
    And the user clicks the button/link    jQuery=li:nth-child(4) button:contains("Add person")
    When the user adds new collaborator
    And the user clicks the button/link    jquery=button:contains("Save Changes")
    Then the user should be redirected to the correct page    ${APPLICATION_TEAM_URL}
    [Teardown]    User closes the browser

The user should not create new org but should follow the create account flow
    [Documentation]    INFUND-1463
    ...
    ...    This test checks if the invited partner who are in the same organisation they can go directly to the create account and they don't have to create an organisation first.
    [Tags]
    [Setup]    The guest user opens the browser
    When the user opens the mailbox and accepts the invitation to collaborate
    And the user should see the text in the page    Join an application
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And the user should see the text in the page    Your organisation
    And the user should see the text in the page    Business Organisation
    And the user should see the element    link=email the application lead
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user fills the create account form    Roger    Axe
    And the user opens the mailbox and verifies the email from
    And the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}

*** Keywords ***
the applicant enters valid inputs
    Click Element    jquery=li:nth-last-child(1) button:contains('Add additional partner organisation')
    Input Text    name=organisations[3].organisationName    Fannie May
    Input Text    name=organisations[3].invites[0].personName    Adrian Booth
    Input Text    name=organisations[3].invites[0].email    worth.email.test+inviteorg1@gmail.com
    focus    jquery=button:contains("Begin application")
    Click Element    jquery=button:contains("Begin application")

The lead applicant should have the correct status
    page should contain element    css=#content h2.heading-medium
    ${input_value} =    get text    css=#content h2.heading-medium
    Should Be Equal As Strings    ${input_value}    Empire Ltd (Lead organisation)
    page should contain link    Steve Smith
    ${input_value} =    get text    css=.list-bullet li small
    Should Be Equal As Strings    ${input_value}    (Lead Applicant)

the user adds new collaborator
    Wait Until Element Is Visible    css=li:nth-child(4) tr:nth-of-type(2) td:nth-of-type(1) input
    Input Text    css=li:nth-child(4) tr:nth-of-type(2) td:nth-of-type(1) input    Roger Axe
    Input Text    css=li:nth-child(4) tr:nth-of-type(2) td:nth-of-type(2) input    worth.email.test+inviteorg2@gmail.com
    focus    jquery=li:nth-child(1) button:contains('Add person')
    sleep    300ms

The status of the invited people should be correct in the application team page
    Element Should Contain    css=#content ul li:nth-child(1)    (Lead Applicant)
    Element Should Contain    css=#content ul li:nth-child(2)    (pending)
    Element Should Contain    css=p+ div .heading-medium small    (Lead organisation)
    Element Should Contain    css=div+ div .heading-medium small    (pending)

the invited collaborators are not editable
    #Element Should Contain    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(3)    (pending)
    page should contain element    jQuery=li:nth-child(1) tr:nth-of-type(1) td:nth-child(1) [readonly]
    page should contain element    jQuery=li:nth-child(1) tr:nth-of-type(1) td:nth-child(2) [readonly]
    page should contain element    jQuery=li:nth-child(2) tr:nth-of-type(1) td:nth-child(1) [readonly]
    page should contain element    jQuery=li:nth-child(2) tr:nth-of-type(1) td:nth-child(2) [readonly]
    page should contain element    jQuery=li:nth-child(3) tr:nth-of-type(1) td:nth-child(2) [readonly]
    page should contain element    jQuery=li:nth-child(3) tr:nth-of-type(1) td:nth-child(1) [readonly]

the applicant cannot assign to pending invitees
    Click Element    jQuery=button:contains("Assigned to")
    Page Should not Contain Element    jQuery=button:contains("Adrian Booth")

the status of the people should be correct in the Manage contributors page
    Element Should Contain    css=li:nth-child(1) tr:nth-of-type(1) td:nth-child(3)    Lead applicant
    Element Should Contain    css=li:nth-child(1) tr:nth-of-type(2) td:nth-child(3)    (pending)

the lead applicant logs out
    Logout as user

the invited user verifies their email
    the user navigates to the page    ${verify_link_4}

the user changes their company name
    (still to implement)

the new company name should be shown throughout the application
    {still to implement}

the lead applicant logs back in
    guest user log-in    &{lead_applicant_credentials}

user selects the radio button
    [Arguments]    ${RADIO_BUTTON}    ${ORG_TYPE}
    Select Radio Button    ${RADIO_BUTTON}    ${ORG_TYPE}

the user can see the updated company name throughout the application
    the user navigates to the page    ${FINANCES_OVERVIEW_URL}
    the user should see the text in the page    NOMENSA LTD
    the user navigates to the page    ${APPLICATION_TEAM_URL}
    the user should see the text in the page    NOMENSA LTD

the user selects the same address
    select Checkbox    id=address-same