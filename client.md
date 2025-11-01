### Nav Bar

![image.png](attachment:0c034608-d4a6-4bfc-a5cf-2aee260fc81b:image.png)

- Layout: Use the style in the image
- Fields:

  There are three buttons

    - Left: Home Button
    - Mid: Chat Button
    - Right: Profile Button
- Interaction:
    - Default Material 3 nav button design
- Nav Logic:
    - On Tap Home, Navigate to Home Page
    - On Tap Chat, Navigate to Chat Page
    - On Tap Profile, Navigate to Profile Page

### Page

- Home Page

  ![navbar_ui.jpg](navbar_ui.jpg)

    - Layout:

      use the general layout style from the image, but follow the detail below to create exact layout.

        - Top section: Task List
            - Task group by date, to form a Task List, presented as Card
            - Each Task List Card has date, and a list of Tasks, vertically stacked
            - Each Task should be presented as a checkbox with Task title, and have a small label of {start time: end time} under it
            - There should be multiple Task List Card, horizontally aligned, with default being leftmost card, overflowing should be cut by screen
        - Bottom Section: Goal List
            - Presented each Goal as Card, vertically stacked
            - Each Goal Card shows Goal name, progress, and has a small color label on the left
    - Interaction:
        - When user swipe left/right on Task List card, should be scroll to another Task List Card with snappy effect
        - When user click task checkbox, the Task should be crossed and color ofTask should change to light grey
        - When user press a goal, it should be draggable, and change the order of the goal would directly change its priority(for every swap in order, do swap in priority)
    - Nav Logic
        - When user tap a goal, navigate to Goal Tree Page with reference to the goal
        - When user tap a task (outside of checkbox), navigate to Task Page
- Task Page/Task Edit Page

  ![page_ui.jpg](page_ui.jpg)
    - Layout: A calendar like page
    - Fields:
        - Title: string
        - Date: date
        - Estimated Hours: double
        - Done: boolean
        - Description: text box
    - Interaction:
        - Every field is clickable, but not editable in Task Page
        - Every field is editable in Task Edit Page
    - Nav Logic:
        - On Task Page, there will not be a lower bar(cancel, save)
        - On click any button, user navigate from Task Page to Task Edit Page. There will be a lower bar at the bottom (cancel, save button)
        - On Task Edit Page, If the value changes, but user does not click save(either back gesture or cancel), pop warn model to warn user changes not save, and then navigate back to Task Page.
        - On Task Edit Page, if the value not changes and user click cancel or back gesture, or the value changes but user click save, navigate straight back to Task Page.
- Milestone Page/Milestone Edit Page
    - Layout: Same as Task Page/ Task Edit Page
    - Fields:
        - Title: string
        - Date: date
        - Estimated Hours: double
        - Status: enum`('blocked', 'in_progress', 'finished')`
        - Description: text box
    - Interaction:
        - Every field is clickable, but not editable in Milestone Page
        - Every field is editable in Milestone Edit Page
    - Nav Logic:
        - On Milestone Page, there will not be a lower bar(cancel, save)
        - On click any button, user navigate from Milestone Page to Milestone Edit Page. There will be a lower bar at the bottom (cancel, save button)
        - On Task Edit Page, If the value changes, but user does not click save(either back or cancel), pop warn model to warn user changes not save, and then navigate back to Milestone Page.
        - On Milestone Edit Page, if the value not changes and user click cancel or back gesture, or the value changes but user click save, navigate straight back to Milestone Page.
        - Milestone Edit Page is the leaf of the navigation tree, it can only navigate back to Milestone Page
- Goal Page/ Goal Edit Page
    - Layout: Same as Task Page/ Task Edit Page
    - Fields:
        - Title: string
        - Start Time: time (view only)
        - Description
        - Context: text area
        - Color: color picker
    - Interaction:
        - Every field is clickable, but not editable in Goal Page
        - Every field is editable in Goal Edit Page
    - Nav Logic:
        - On Goal Page, there will not be a lower bar(cancel, save)
        - On click any button, user navigate from Goal Page to Goal Edit Page. There will be a lower bar at the bottom (cancel, save button)
        - On Goal Edit Page, If the value changes, but user does not click save(either back or cancel), pop warn model to warn user changes not save, and then navigate back to Goal Page.
        - On Goal Edit Page, if the value not changes and user click cancel or back gesture, or the value changes but user click save, navigate straight back to Goal Page.
        - Goal Edit Page is the leaf of the navigation tree, it can only navigate back to previous Page
- Profile Page/Profile Edit Page
    - Layout: Similar to Task Page/ Task Edit Page
    - Field:
        - Profile Picture: Circle Image align to left
        - Name: text
        - email: text
        - Available Hours per Week: int
    - Interaction:
        - Every field is clickable, but not editable in Profile Page
        - Only Available Hours Per Week is editable in Profile Edit Page
    - Nav Logic:
        - On Profile Page, there will not be a lower bar(cancel, save)
        - Only when tapping Available Hours Per Week, will the user be navigated to Profile Edit Page. There will be a lower bar at the bottom (cancel, save button)
        - On Profile Edit Page, If the value changes, but user does not click save(either back or cancel), pop warn model to warn user changes not save, and then navigate back to Profile Page.
        - On Profile Edit Page, if the value not changes and user click cancel or back gesture, or the value changes but user click save, navigate straight back to Profile Page.
        - Profile Edit Page is the leaf of the navigation tree, it can only navigate back to previous page
- Goal Tree Page
    - Layout: Toggle-able Tree structure, like canvas

      ![tree_ui.jpg](tree_ui.jpg)

    - Fields:
    - Interaction:
        - Sticky Title
            - A goal will stack on top of anything before it, and sticky to the top.
            - A milestone will stack on top of any tasks and milestones, and sticky after a goal.
            - A task will stack on top of any tasks, and sticky after a milestone.
        - Toggle-able Title
            - A arrow, as a button, on the right of the title box, that can toggle/un-toggle
        - Clickable Goal/Milestone/Task Title
            - The Title text is clickable button
        - “+” buttons: Located at the left start of the Title box of a Goal or Milestone.
        - Add Goal Button: Top right of the Goal Tree Page.
    - Nav Logic:
        - On tap the Goal/Milestone/Task Title text, navigate to Goal/Milestone/Task Page
        - On tap the “+” button on each title, if it’s a Goal title, navigate to Milestone Page, then immediately to milestone edit page; if it’s a milestone title, navigate to Task Page, then immediately to Task Edit Page.
- Goal Tree Preview Page
    - Layout: Same as Goal Tree Page
    - Fields: Same as Goal Tree Page
    - Interaction:
        - With one close button on top left, no Add Goal Button
        - Others same as Goal Tree Page
    - Nav Logic: No navigation. It’s a view only page. It’s a leaf of navigation tree, can only be popped from navigation stack.
- Chat Page
    - Layout:

      Generally follows the Chat GPT’s layout, user message at right, AI response at left, text input at bottom

      ![chat_ui.jpg](chat_ui.jpg)

        - Back Button: On top left corner
        - Goal Preview button: If there is a plan generated, the preview button should be located under the text response of AI, like where the copy/thumbs up buttons are.
    - Interaction:
        - Entire Chat should be scrollable
    - Nav Logic:
        - On Tap Back button: Navigate back
        - On Tap Preview button: Navigate to Goal Tree Preview Page

### Modal

- Error Modal
    - This Modal should only have a button to confirm and close the modal (right bottom corner)
- Warning Modal
    - This Modal should only have a button to confirm and close the modal (right bottom corner)
- Choose Modal
    - This Model should have two buttons, and a text field that describe the options