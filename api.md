This is the backend API endpoint expected input and response format. Follow this when designing any API related services.
We’ll cover all 6 user-facing API domains from your earlier design:
1.	Auth / Profile
2.	Home Dashboard
3.	Goals
4.	Milestones
5.	Tasks
6.  AI Agent

⸻

🧑‍💼 1️⃣ Onboarding & Authentication

GET /v1/me

Purpose: Fetch current user profile (JWT-authenticated).
Input: None

Response 200
```json
{
    "data": {
        "id": "u_001",
        "name": "Lenny",
        "email": "lenny@example.com",
        "profileImageUrl": "https://cdn.app/avatar/lenny.png",
        "timezone": "America/Chicago",
        "availableHoursPerWeek": "30", 
        "createdAt": "2025-10-20T18:00:00Z",
        "updatedAt": "2025-10-25T12:00:00Z"
    }
}

```


⸻

PATCH /v1/me

Purpose: Update user profile

Request Body
```json
{
    "name": "Lenny Zhang",
    "timezone": "America/New_York",
    "availableHoursPerWeek": "25",
    "profileImageUrl": "https://cdn.app/avatar/new.png"
}

```

Response 200
```json
{
    "data": {
        "id": "u_001",
        "name": "Lenny Zhang",
        "email": "lenny@example.com",
        "profileImageUrl": "https://cdn.app/avatar/new.png",
        "timezone": "America/New_York",
        "availableHoursPerWeek": "25",
        "createdAt": "2025-10-20T18:00:00Z",
        "updatedAt": "2025-10-25T15:00:00Z"
    }
}

```


⸻

🏠 2️⃣ Home Dashboard

GET /v1/tasks:query?day=2025-11-01

Purpose: Get all tasks for a specific date

Response 200
```json
{
    "data": [
        {
            "id": "t_001",
            "milestoneId": "m_456",
            "title": "UI Layout",
            "description": "Implement main layout",
            "date": "2025-11-01",
            "estimatedHours": 3,
            "status": "not_yet_done",
            "createdAt": "2025-10-25T12:00:00Z",
            "updatedAt": "2025-10-25T12:00:00Z"
        },
        {
            "id": "t_002",
            "milestoneId": "m_456",
            "title": "Color Palette",
            "date": "2025-11-01",
            "estimatedHours": 2,
            "status": "done",
            "createdAt": "2025-10-22T10:00:00Z",
            "updatedAt": "2025-10-25T09:00:00Z"
        }
    ]
}
```


⸻

GET /v1/goals?status=active

Purpose: List active goals for the user at Home Page

Response 200
```json

{
    "data": [
          {
            "id": "g_001",
            "title": "Build MVP",
            "priority": 1,
            "color": -65500,
            "totalTaskHours": 10,
            "doneTaskHours": 4
          },
          {
            "id": "g_002",
            "title": "Improve UX",
            "priority": 2,
            "color": -65500,
            "progress": 0.13,
            "totalTaskHours": 5,
            "doneTaskHours": 1
          }
    ]
}
```

Response 409 Conflict
```json
{
  "error": {
    "code": 409,
    "message": "Available hours (25h/week) are insufficient for current active goals (32h/week required).",
    "details": {
      "availableHoursPerWeek": 25,
      "requiredHoursPerWeek": 32,
      "conflictingGoals": [
        { "goalId": "g_123", "title": "Build Portfolio Website", "weeklyHours": 18 },
        { "goalId": "g_456", "title": "Study for GRE", "weeklyHours": 14 }
      ]
    }
  }
}
```

⸻

🎯 3️⃣ Goal Management

POST /v1/goals

Purpose: Create new goal

Request
```json
{
    "title": "Learn Kotlin Multiplatform",
    "description": "Develop cross-platform prototype",
    "priority": 1,
    "minHoursPerWeek": "10",
    "color": -65536,
    "context": "I have not use kotlin before.",
    "startDate": "2025-11-01T00:00:00Z",
    "targetDate": "2025-12-15T00:00:00Z"
}

```

Response 201
```json
{
    "data": {
        "id": "g_123",
        "title": "Learn Kotlin Multiplatform",
        "description": "Develop cross-platform prototype",
        "priority": 1,
        "minHoursPerWeek": "10",
        "color": -65536,
        "context": "I have not use kotlin before.",
        "status": "active",
        "startDate": "2025-11-01T00:00:00Z",
        "targetDate": "2025-12-15T00:00:00Z",
        "createdAt": "2025-10-25T18:00:00Z",
        "updatedAt": "2025-10-25T18:00:00Z"
    }
}

```

⸻

GET /v1/goals/{goalId}

Purpose: View metadata of a goal

Response 200

```json
{
    "data": {
        "id": "g_123",
        "title": "Learn Kotlin Multiplatform",
        "description": "Develop cross-platform prototype",
        "priority": 1,
        "minHoursPerWeek": "10",
        "color": -65536,
        "context": "I have not use kotlin before.",
        "status": "active",
        "startDate": "2025-11-01T00:00:00Z",
        "targetDate": "2025-12-15T00:00:00Z"
    }
}

```

⸻

GET /v1/goals/{goalId}/metrics

Purpose: Show the progress of a certain goal

Response 200
```json
{
    "data": {
        "goalId": "g_001",
        "totalTaskHours": 10,
        "doneTaskHours": 4
    }
}

```

⸻

PATCH /v1/goals/{goalId}

Purpose: Update goal

Request
```json
{
    "title": "Learn KMP with Firebase",
    "status": "active",
    "color": -45536
}

```

Response 200

```json
{
    "data": {
        "id": "g_123",
        "title": "Learn KMP with Firebase",
        "status": "active",
        "color": -45536,
        "updatedAt": "2025-10-25T19:00:00Z"
    }
}

```

⸻

🪜 4️⃣ Milestone Management

GET /v1/goals/{goalId}/milestones

Purpose: On goal page, show the milestones under a goal

Response 200
```json

{
    "data": [
          { "id": "m_789", "title": "Finish Kotlin Basics", "status": "in_progress" },
          { "id": "m_790", "title": "Build a mobile app", "status": "not_started" }
    ]
}
```

⸻

POST /v1/goals/{goalId}/milestones

Purpose: Create new milestones

Request
```json
{
    "title": "Build core UI",
    "description": "Implement Home, Goals, and Task pages",
    "parentMilestoneId": null
}
```

Response 201
```json
{
    "data": {
        "id": "m_789",
        "title": "Build core UI",
        "description": "Implement Home, Goals, and Task pages",
        "status": "blocked",
        "createdAt": "2025-10-25T18:30:00Z"
    }
}
```

⸻

GET /v1/milestones/{milestoneId}

Purpose: View metadata of a milestone

Response 200

```json
{
    "data": {
        "id": "m_789",
        "title": "Learn Kotlin Multiplatform",
        "description": "Implement Home, Goals, and Task pages",
        "status": "in_progress"
    }
}

```

⸻
PATCH /v1/milestones/{milestoneId}

Purpose: Update current milestones

Request
```json
{ "status": "in_progress" }
```

Response

```json
{
    "data": {
        "id": "m_789",
        "goalId": "g_123",
        "status": "in_progress",
        "updatedAt": "2025-10-25T20:00:00Z"
    }
}
```


⸻

GET /v1/milestones/{milestoneId}/metrics

Purpose: On goal page/ milestone page, show milestone's progress (complete hours/ total hours)

Response 200
```json
{
    "data": {
        "milestoneId": "m_789",
        "totalTaskHours": 10,
        "doneTaskHours": 6
    }
}

```


⸻

✅ 5️⃣ Task Management


POST /v1/milestones/{milestoneId}/tasks

Purpose: On goal page/ milestone page, show all tasks under a milestone

Request
```json
{
    "title": "Implement Home Screen",
    "description": "Task list and goal progress UI",
    "date": "2025-11-02",
    "estimatedHours": 4,
    "status": "not_yet_done"
}

```

Response 201
```json
{
    "data": {
        "id": "t_789",
        "milestoneId": "m_456",
        "title": "Implement Home Screen",
        "date": "2025-11-02",
        "estimatedHours": 4,
        "status": "not_yet_done",
        "createdAt": "2025-10-25T18:40:00Z"
    }
}

```
⚠️ Response — 409 Conflict
```json
{
  "error": {
    "code": 409,
    "message": "Task date conflicts with an existing scheduled task or calendar event.",
    "details": {
      "conflictingTaskId": ["t_654", "t_655"]
    }
  }
}
```
⸻

GET /v1/tasks/{taskId}

Request
```json
{
    "status": "done",
    "date": "2025-11-03"
}
```

Response
```json
{
    "data": {
        "id": "t_789",
        "status": "done",
        "date": "2025-11-03",
        "updatedAt": "2025-10-25T20:00:00Z"
    }
}

```
⸻


PATCH /v1/tasks/{taskId}

Request
```json
{
"status": "done",
"date": "2025-11-03"
}
```

Response 200
```json

{
"data": {
"id": "t_789",
"status": "done",
"date": "2025-11-03",
"updatedAt": "2025-10-25T20:00:00Z"
}
}

```

Response 409
```json
{
  "error": {
    "code": 409,
    "message": "Cannot update task. The new date conflicts with another task under the same milestone.",
    "details": {
      "conflictingTaskId": ["t_710", "t_711"]
    }
  }
}
```


⸻

POST /v1/tasks/{taskId}:done

Request
```json
{ "doneAt": "2025-10-25T22:00:00Z" }

```

Response 200
```json
{
"data": {
"id": "t_789",
"status": "done",
"updatedAt": "2025-10-25T22:00:00Z"
}
}

```


⸻

POST /v1/tasks/{taskId}:undone

Response 200
```json
{
"data": {
"id": "t_789",
"status": "not_yet_done",
"updatedAt": "2025-10-25T22:10:00Z"
}
}

```


⸻

🤖6️⃣AI Agent

🧩 Overview

Purpose:
This API powers Gritto’s conversational goal creation flow.
The backend orchestrates the GoalCreationWorkflow, where Gemini helps users describe, refine, and confirm their goals — producing a structured goal → milestones → tasks plan.

⸻

⚙️ Agent Workflow Summary

Step	Endpoint	Role	Agent Action
1️⃣	/v1/agent/goal/session:start	Initialize conversation	GreetingAgent
2️⃣	/v1/agent/goal/session:message	Handle user input (generate or iterate plan)	GeneratePlanAgent / LoopAgent
3️⃣	/v1/agent/goal/session:consent	User approves the plan	ConsentAgent
4️⃣	/v1/agent/goal/session:finalize	Save goal and close session	FinalizeAgent


⸻

🧠 1️⃣ POST /v1/agent/goal/session:start

Purpose:
Start a new goal creation session.
Triggers the GreetingAgent to prompt user for a new goal.

📨 Request
```json
{
"userId": "u_001"
}

```

📤 Response
```json
{
"sessionId": "sess_goal_001",
"reply": "Hi there! What do you want to achieve?",
"state": {
"step": "greeting",
"intent": "goal_creation",
"sessionActive": true
}
}

```


⸻

💬 2️⃣ POST /v1/agent/goal/session:message

Purpose:
Send user input during goal creation chat.
This endpoint drives the iterative planning loop handled by LoopAgent.

Depending on context:
•	If no plan yet → GeneratePlanAgent creates one.
•	If plan exists → ReceiveFeedbackAgent refines it.
•	Loop continues until user approval.

📨 Request
```json
{
"sessionId": "sess_goal_001",
"userId": "u_001",
"message": "I want to build my personal portfolio website.",
"context": {
"existingGoals": [
{ "id": "g_101", "title": "Learn React", "status": "active" }
],
"calendarEvents": [
{
"title": "Work Meeting",
"start": "2025-11-03T14:00:00Z",
"end": "2025-11-03T15:00:00Z"
}
]
},
"state": {
"currentPlan": null
}
}

```


⸻

📤 Case A — First Plan Proposal
```json
{
"reply": "Here’s a structured plan for building your portfolio website!",
"proposedPlan": {
"goal": {
"title": "Build Portfolio Website",
"description": "A personal site to showcase projects.",
"priority": 1
},
"milestones": [
{
"title": "Design Phase",
"tasks": [
{ "title": "Create wireframes", "date": "2025-11-04", "estimatedHours": 3 },
{ "title": "Choose color palette", "date": "2025-11-05", "estimatedHours": 2 }
]
},
{
"title": "Development Phase",
"tasks": [
{ "title": "Implement homepage", "date": "2025-11-07", "estimatedHours": 4 },
{ "title": "Add project section", "date": "2025-11-09", "estimatedHours": 3 }
]
}
]
},
"state": {
"step": "plan_generated",
"iteration": 1,
"sessionActive": true
}
}

```


⸻

📤 Case B — User Requests Revision
```json
{
"reply": "Got it! I’ve moved the design phase to next week as requested.",
"updatedPlan": {
"goal": { "title": "Build Portfolio Website" },
"milestones": [
{
"title": "Design Phase",
"tasks": [
{ "title": "Create wireframes", "date": "2025-11-10", "estimatedHours": 3 },
{ "title": "Choose color palette", "date": "2025-11-11", "estimatedHours": 2 }
]
}
]
},
"state": {
"step": "plan_iteration",
"iteration": 2,
"sessionActive": true
}
}

```

⸻

📤 Case C — User Approves Plan

If user says “Looks good” or “Let’s save this plan”,
the backend detects approval (via CheckApprovalAgent) and prompts confirmation.
```json
{
"reply": "Perfect! Shall I save this plan as your new goal?",
"requiresConsent": true,
"state": {
"step": "approval_check",
"intent": "goal_creation",
"sessionActive": true
}
}

```


⸻

🪄 3️⃣ POST /v1/agent/goal/session:consent

Purpose:
Triggered when the user clicks “Confirm” in the UI.
Marks loop termination and passes control to FinalizeAgent.

📨 Request
```json
{
"sessionId": "sess_goal_001",
"userId": "u_001",
"action": "approve"
}

```

📤 Response
```json
{
"reply": "Great! I’ll save your goal now.",
"state": {
"step": "consent_received",
"sessionActive": true
}
}

```


⸻

🗄️ 4️⃣ POST /v1/agent/goal/session:finalize

Purpose:
Store the final structured plan in Firestore and end the session.

📨 Request
```json
{
"sessionId": "sess_goal_001",
"userId": "u_001",
"finalPlan": {
"goal": {
"title": "Build Portfolio Website",
"description": "A personal website to showcase projects."
},
"milestones": [
{
"title": "Design Phase",
"tasks": [
{ "title": "Create wireframes", "date": "2025-11-10" },
{ "title": "Choose color palette", "date": "2025-11-11" }
]
}
]
}
}

```

📤 Response
```json
{
"reply": "Your goal 'Build Portfolio Website' has been created successfully!",
"storedGoalId": "g_new_2025_001",
"state": {
"step": "finalized",
"sessionClosed": true
}
}

```


⸻

🧩 Example Full Flow Summary

Step	User Action	Backend Endpoint	Agent Logic	Response
1️⃣	Open chat	/session:start	GreetingAgent	“What do you want to achieve?”
2️⃣	“I want to build my portfolio site”	/session:message	GeneratePlanAgent	Plan proposal
3️⃣	“Can we move design to next week?”	/session:message	LoopAgent iteration	Revised plan
4️⃣	“Looks good!”	/session:message	CheckApprovalAgent → ConsentAgent	Confirmation prompt
5️⃣	Click confirm	/session:consent	ConsentAgent	“Saving your plan…”
6️⃣	—	/session:finalize	FinalizeAgent	“Goal created successfully!”


⸻

🧭 State Lifecycle Example
```json
{
"sessionId": "sess_goal_001",
"userId": "u_001",
"state": {
"intent": "goal_creation",
"step": "plan_iteration",
"iteration": 3,
"currentPlan": { ... },
"sessionActive": true
}
}

```


⸻

✅ Summary

Domain	Endpoint	Purpose
Auth	/v1/me	Fetch/update profile
Home	/v1/tasks:query, /v1/goals	Today’s tasks & goal progress
Goals	/v1/goals, /v1/goals/{id}	CRUD + hierarchy
Milestones	/v1/goals/{id}/milestones, /v1/milestones/{id}	CRUD + metrics
Tasks	/v1/milestones/{id}/tasks, /v1/tasks/{id}	CRUD + mark done/undone

