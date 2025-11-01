🌱 Gritto: AI Goal Planning Companion

Overview

Gritto is a cross-platform AI-powered app that helps users turn their ambitions into structured, actionable plans.
The app focuses on one core feature: AI-assisted goal creation, where the user describes what they want to achieve, and Gritto automatically generates a structured plan with goals, milestones, and tasks.

Built for the Google Cloud Run Hackathon, Gritto demonstrates how Gemini models, Google ADK, and serverless architecture can power intelligent multi-turn AI workflows — deployed in minutes on Cloud Run.

⸻

🎯 Vision

Many people know what they want to do but not how to start.
Gritto bridges that gap — turning vague ambitions into step-by-step execution plans through natural conversation.

The Core Loop
1.	Describe: The user opens chat and describes their goal.
2.	Generate: Gritto’s AI Agent builds a structured plan (goal → milestones → tasks).
3.	Iterate: The user gives feedback — “Can we move design to next week?” — and the AI adjusts.
4.	Confirm: Once approved, the plan is saved and displayed as a Goal Tree.

This cycle enables users to go from idea → plan → action in one conversation.

⸻

💡 Key Agent

Agent	Type	Role
🧠 GoalCreationAgent	Sequential + Loop Agent	Guides the conversation, generates plans, iterates on feedback, and finalizes structured goal data.

Workflow Summary

Step	Agent	Description
1️⃣	GreetingAgent	Welcomes the user and asks “What do you want to achieve?”
2️⃣	GeneratePlanAgent	Creates the first plan draft using Gemini based on user input and current context (existing goals + calendar).
3️⃣	LoopAgent	Iterates on user feedback, refining milestones and tasks.
4️⃣	FinalizeAgent	Saves the confirmed plan to Firestore and ends the session.


⸻

⚙️ Tech Stack

Layer	Technology	Description
Frontend (Mobile)	Kotlin Multiplatform (Compose Multiplatform)	Unified Android/iOS UI with native navigation and chat interface.
Backend (Serverless)	TypeScript + Express (Cloud Run)	API gateway for users, tasks, milestones, and AI agent orchestration.
AI Layer	Gemini 1.5 Flash + Google ADK	Runs the GoalCreationWorkflow (Sequential + Loop Agent pipeline).
Database	Firestore	Stores user data, generated plans, and sessions.
Deployment	Google Cloud Run	Fast, scalable, fully managed deployment with containerized services.


⸻

🚀 Hackathon Goals (MVP)

Goal	Deliverable
1️⃣ Mobile Chat UI	Kotlin Compose chat interface integrated with /v1/agent/goal/session:* APIs.
2️⃣ AI Agent Backend	Cloud Run API connected to Gemini and ADK.
3️⃣ Goal Creation Workflow	Multi-turn goal generation + refinement conversation.
4️⃣ Firestore Integration	Store generated goals and tasks.
5️⃣ Demo Deployment	Live Cloud Run endpoint + working Android build.


⸻

🧭 Future Roadmap
•	Phase 2: Add Calendar sync (AI conflict avoidance)
•	Phase 3: Introduce Reflection & Motivation agents
•	Phase 4: Collaborative goal planning & shared milestones

⸻

🧩 Differentiation

Unlike static goal planners, Gritto uses conversational AI to create realistic, personalized plans.

Feature	Gritto Advantage
Conversational Goal Setup	LLM-guided dialogue instead of forms
Auto-Structured Plans	Generates hierarchy (goal → milestone → task) automatically
Iterative Refinement	Dynamic loop for adjusting plans
Serverless Scalability	Deployed in minutes with Cloud Run


⸻

🏁 Summary

Gritto transforms goal-setting into an intelligent, conversational experience.
By combining Gemini’s reasoning, ADK’s agent orchestration, and Cloud Run’s scalability, it shows how simple ideas can evolve into actionable systems — in minutes, not months.

Build your plan. Iterate with AI. Execute with Gritto.
