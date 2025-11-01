# AI Agent Workflow

🧠 Gritto: Goal Creation Workflow (ADK Pseudocode)

💡 Pattern Used

SequentialAgent + LoopAgent (Iterative Refinement Pattern)
Based on your workflow:

“User describes a goal → LLM proposes plan → user gives feedback → LLM refines plan → repeat until approved → save to DB.”

⸻

🧩 Agent Hierarchy

GoalCreationWorkflow (SequentialAgent)
│
├── GreetingAgent (LlmAgent)
│      └── Asks: "What do you want to achieve?"
│
├── PlanIterationLoop (LoopAgent)
│      ├── GeneratePlanAgent (LlmAgent)
│      ├── ReceiveFeedbackAgent (LlmAgent)
│      └── CheckApprovalAgent (CustomAgent)
│
└── FinalizeAgent (CustomAgent)
└── Saves plan and closes session


⸻

⚙️ 1️⃣ Pseudocode — Agent Definitions (Python-style, ADK Syntax)

🧠 Greeting Agent
```python
    from google.adk.agents import LlmAgent, SequentialAgent, LoopAgent, BaseAgent
    from google.adk.events import Event, EventActions
    from google.adk.agents.invocation_context import InvocationContext
    from typing import AsyncGenerator
    
    greeting_agent = LlmAgent(
    name="GreetingAgent",
    instruction="Start the session by greeting the user and asking: 'What do you want to achieve?'",
    output_key="user_goal_text"
    )

```


⸻

🏗️ Generate Plan Agent
```python
    generate_plan_agent = LlmAgent(
    name="GeneratePlanAgent",
    instruction=(
    "The user has described their goal in {user_goal_text}. "
    "Using this input and session.state['context'] (user goals + calendar), "
    "propose a structured plan conforming to the Gritto data model. "
    "Store it in session.state['proposed_plan'] with nested fields: goal → milestones → tasks."
    ),
    output_key="proposed_plan"
    )

```


⸻

💬 Receive Feedback Agent
```python
receive_feedback_agent = LlmAgent(
name="ReceiveFeedbackAgent",
instruction=(
"Ask the user if they would like to adjust the plan. "
"Respond empathetically to feedback. "
"If user provides suggestions, update session.state['user_feedback']."
),
output_key="user_feedback"
)

```


⸻

🔁 Check Approval Agent (controls loop termination)
```python
class CheckApprovalAgent(BaseAgent):
async def _run_async_impl(self, ctx: InvocationContext) -> AsyncGenerator[Event, None]:
feedback = ctx.session.state.get("user_feedback", "").lower()
# Stop loop if user approves or says "yes"
is_approved = any(keyword in feedback for keyword in ["approve", "looks good", "yes", "okay"])
yield Event(
author=self.name,
actions=EventActions(escalate=is_approved),
content=("User approved the plan." if is_approved else "Continue iteration.")
)

```


⸻

🌀 Plan Iteration Loop Agent
```python
plan_iteration_loop = LoopAgent(
    name="PlanIterationLoop",
    max_iterations=5,
    sub_agents=[
        generate_plan_agent,
        receive_feedback_agent,
        CheckApprovalAgent(name="CheckApprovalAgent")
    ]
)

```

Loop Behavior:
1.	GeneratePlanAgent proposes a structured plan.
2.	ReceiveFeedbackAgent asks user for feedback.
3.	CheckApprovalAgent checks if feedback contains approval keywords.
4.	If approved → escalate=True → exit loop.
5.	If not → loop repeats for another refinement cycle (up to 5 times).

⸻

🧾 Finalize Agent
```python
class FinalizeAgent(BaseAgent):
    async def _run_async_impl(self, ctx: InvocationContext) -> AsyncGenerator[Event, None]:
    plan = ctx.session.state.get("proposed_plan")
    user_id = ctx.session.state.get("user_id")

    # Pseudo Firestore save (in actual system, you'd call your backend here)
    save_plan_to_firestore(user_id, plan)

    yield Event(
        author=self.name,
        content=f"Goal '{plan['goal']['title']}' saved successfully! 🎯",
        actions=EventActions(escalate=True)
    )


class FinalizeAgent(BaseAgent):
    async def _run_async_impl(self, ctx: InvocationContext) -> AsyncGenerator[Event, None]:
    plan = ctx.session.state.get("proposed_plan")
    user_id = ctx.session.state.get("user_id")

    # Pseudo Firestore save (in actual system, you'd call your backend here)
    save_plan_to_firestore(user_id, plan)

    yield Event(
        author=self.name,
        content=f"Goal '{plan['goal']['title']}' saved successfully! 🎯",
        actions=EventActions(escalate=True)
    )
```

⸻

🧩 Combine into GoalCreationWorkflow
```
goal_creation_workflow = SequentialAgent(
name="GoalCreationWorkflow",
sub_agents=[
greeting_agent,
plan_iteration_loop,
FinalizeAgent(name="FinalizeAgent")
]
```
)


⸻

🧭 2️⃣ Invocation Flow

1️⃣ Session Start

session = start_new_session(state={"user_id": "u_001", "context": user_context})
await goal_creation_workflow.run_async(session)

2️⃣ Shared State Flow

Step	State Keys Added	Description
GreetingAgent	user_goal_text	User’s description of goal
GeneratePlanAgent	proposed_plan	Structured plan JSON
ReceiveFeedbackAgent	user_feedback	Feedback message
CheckApprovalAgent	—	Controls loop
FinalizeAgent	—	Saves plan to Firestore


⸻

🧩 3️⃣ State Evolution Example

Step	Agent	Key Session State	Example Value
1️⃣	GreetingAgent	user_goal_text	"I want to build a personal website."
2️⃣	GeneratePlanAgent	proposed_plan	{ goal: {...}, milestones: [...], tasks: [...] }
3️⃣	ReceiveFeedbackAgent	user_feedback	"Can we add a milestone for SEO setup?"
4️⃣	CheckApprovalAgent	—	escalate=False → loop continues
5️⃣	GeneratePlanAgent (again)	proposed_plan	Updated with SEO milestone
6️⃣	ReceiveFeedbackAgent	user_feedback	"Looks good!"
7️⃣	CheckApprovalAgent	—	escalate=True → exit loop
8️⃣	FinalizeAgent	—	Saves final plan & sends completion message


⸻

🧩 4️⃣ Example Structured Plan (LLM Output)

Returned from GeneratePlanAgent → stored in session.state['proposed_plan'].
```json
{
"goal": {
"title": "Build Portfolio Website",
"description": "A personal website to showcase my projects.",
"priority": 1
},
"milestones": [
{
"title": "Design Phase",
"tasks": [
{ "title": "Create wireframes", "date": "2025-11-03", "estimatedHours": 3 },
{ "title": "Choose color palette", "date": "2025-11-04", "estimatedHours": 2 }
]
},
{
"title": "Development Phase",
"tasks": [
{ "title": "Implement homepage", "date": "2025-11-06", "estimatedHours": 4 },
{ "title": "Add project section", "date": "2025-11-08", "estimatedHours": 3 }
]
}
]
}
```



⸻

✅ Benefits of Using LoopAgent Here

Advantage	Description
Interactive Refinement	Allows user feedback to improve plan iteratively
Automatic Stop Condition	CheckApprovalAgent stops when user approves
State-Persistent Context	Shared session.state holds current plan + feedback
Composability	Can plug into higher-level multi-agent systems later (e.g., Coordinator Agent)


⸻

🧩 5️⃣ (Optional) Integration to Coordinator Later

When you expand Gritto, this GoalCreationWorkflow can easily become a sub-agent of your global ChatAgent:
```python
    coordinator_agent = LlmAgent(
        name="ChatAgent",
        model="gemini-2.0-flash",
        instruction="Route user requests. For goal creation, transfer to GoalCreationWorkflow.",
        sub_agents=[goal_creation_workflow]
    )
```