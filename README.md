# Gebeta Sovereign Code Assistant

**License:** MIT | **Version:** 1.0.0 | **Platform:** Linux, macOS, Windows  
**Status:** Production Ready

---

## About

Gebeta Sovereign Code Assistant is a local-first AI engineering environment that enables developers and teams to code, review, refactor, test, and execute agent workflows without exposing proprietary source code to third-party AI providers.

 "AI accelerates engineering without removing human control."

---

## Core Philosophy

- **Local First** – Models run locally, code stays local
- **Human Approval** – Sensitive actions require explicit review
- **Controlled Agents** – AI operates inside policy boundaries
- **Auditability** – Engineering actions are reviewable
- **Team Standardization** – Repeatable, governed AI workflows

---

## Features

| Feature | Description |
|---------|-------------|
| 🔒 Local Inference | Run coding models locally using Ollama — no cloud dependency |
| 🤖 Agent-Ready | Multi-step coding workflows with Continue |
| ✅ Human Approval | Explicit review before sensitive actions |
| 🛡️ Project Guardrails | Repo-specific rules and coding standards |
| 📋 Auditability | Local history, approvals, and action logs |
| 👥 Team Standardization | Shared configs and deployment patterns |
| ✈️ Air-Gapped Option | Zero external connectivity required |
| 🎛️ Two Deployment Modes | Maximum Privacy or Productivity Mode |

---

## Quick Start

Get up and running in 10 minutes.

### 1. Clone the Repository

```bash
git clone https://github.com/gebeta/gebeta-sovereign-code-assistant
cd gebeta-sovereign-code-assistant
```

2. Install Ollama

```bash
# macOS/Linux
curl -fsSL https://ollama.com/install.sh | sh

# Windows: Download from https://ollama.com
```

3. Start Ollama & Pull Models

```bash
# Start the Ollama server (keep this terminal open)
ollama serve

# In a new terminal, pull recommended models
ollama pull qwen2.5-coder:7b
ollama pull codellama:7b
```

4. Install VS Code & Continue

· Install Visual Studio Code
· Install the Continue extension

5. Configure Continue

```bash
# macOS/Linux
mkdir -p ~/.continue
cp configs/continue-config.yaml ~/.continue/config.yaml

# Windows
mkdir %USERPROFILE%\.continue
copy configs\continue-config.yaml %USERPROFILE%\.continue\config.yaml
```

6. Start Coding

Open VS Code, open the Continue sidebar (Cmd+Shift+P → "Continue: Open Chat"), and start coding with AI assistance!

---

Deployment Modes

Mode A: Maximum Privacy (Recommended for Sensitive Code)

Best for: Fintech, proprietary IP, compliance-sensitive environments

Stack:

· Ollama (local inference)
· Continue (IDE agent)
· VS Code (telemetry minimized)
· Local terminal only

Setup:

```bash
cp configs/continue-config-safe.yaml ~/.continue/config.yaml
```

Mode B: Productivity Mode

Best for: Multi-agent workflows, faster execution

Stack:

· Ollama + Continue
· Warp terminal (with ZDR enabled)
· Hardened internet-enabled environment

Setup:

```bash
# Install Warp (macOS)
brew install --cask warp

# Enable Zero Data Retention in Warp settings
cp configs/continue-config.yaml ~/.continue/config.yaml
```

---

Model Recommendations

Use Case Model RAM Command
Fast autocomplete qwen2.5-coder:1.5b ~2 GB ollama pull qwen2.5-coder:1.5b
Balanced coding agent qwen2.5-coder:7b ~6 GB ollama pull qwen2.5-coder:7b
General assistant + docs llama3.1:8b ~8 GB ollama pull llama3.1:8b
Low RAM fallback phi3:mini ~2.5 GB ollama pull phi3:mini

---

Security & Trust

What This Protects Against

Risk Mitigation
Third-party AI training on code Local models only
Cloud prompt retention No data sent to hosted providers
Accidental source leakage Air-gappable configuration
Over-permissioned agents Manual approval required
Silent execution Agent asks before running commands

What This Does NOT Protect Against

· Malicious local dependencies (npm, pip, etc.)
· Insecure commands approved by the user
· Compromised OS / endpoint malware
· Package manager supply-chain attacks
· Git remote misconfiguration
· Insider threats

Important: This is a control-first system, not a convenience-first system. Human review is always required.

---

Documentation

Document Description
QUICKSTART.md Get started in 10 minutes
SECURITY_AND_TRUST.md Threat model and trust boundaries
TEAM_DEPLOYMENT.md Scale to your team
USE_CASES.md Real-world examples
WHY_GEBETA.md Founder vision and philosophy
ROADMAP.md Product roadmap
CONTRIBUTING.md How to contribute

---

Repository Structure

```
gebeta-sovereign-code-assistant/
│
├── README.md                 # This file
├── LICENSE                   # MIT License
├── QUICKSTART.md            # Quick start guide
├── SECURITY_AND_TRUST.md    # Security documentation
├── TEAM_DEPLOYMENT.md       # Team setup guide
├── USE_CASES.md             # Example use cases
├── WHY_GEBETA.md            # Founder vision
├── ROADMAP.md               # Product roadmap
├── CONTRIBUTING.md          # Contribution guidelines
├── .gitignore               # Git ignore rules
│
├── configs/                 # Ready-to-use configurations
│   ├── continue-config.yaml
│   ├── continue-config-safe.yaml
│   ├── continue-config-team.yaml
│   ├── continue-config-lowram.yaml
│   ├── gebeta-rules.md
│   └── safe-command-policy.md
│
├── templates/               # Starter templates
│   ├── fastapi-service-template/
│   ├── springboot-service-template/
│   └── react-frontend-template/
│
├── examples/                # Example workflows
│   └── example-agent-prompts.md
│
└── docs/                    # Additional documentation
    ├── architecture.md
    └── deployment-modes.md
```

---

Known Limitations

· Local models may be slower than cloud models
· Agent tool use varies by model and hardware
· Large repositories may require context tuning
· Code quality depends on model choice and human review
· Some actions need repeated approval
· Autocomplete quality may be below premium hosted tools

Positioning: Gebeta promises controlled AI, not perfect AI. That is a stronger promise.

---

Roadmap

Version Focus Timeline
V1 Foundation — Documentation, configs, starter kit ✅ Now
V2 Platform — Web portal, onboarding, analytics Q3 2026
V3 Enterprise — Team control plane, governance, audit dashboard Q1 2027

---

⭐ Support the Project

If Gebeta Sovereign Code Assistant helps you build with control and privacy, please star this repository and share it with your team.

---

Contributing

We welcome contributions! Please see CONTRIBUTING.md for guidelines.

Ways to contribute:

· 🐛 Report bugs
· 💡 Suggest features
· 📝 Improve documentation
· 🔧 Submit code improvements
· 📢 Share with your network

---

License

This project is licensed under the MIT License — see LICENSE for details.

---

Founder

Mohammed B. Kemal
Founder & System Architect, Gebeta Universe

· 🌐 Website: https://gebetauae.com
· 🔗 LinkedIn: https://www.linkedin.com/in/mohammed-b-kemal
· 🐦 Twitter: @gebetasovereign

---

Acknowledgments

· Ollama — Local LLM runtime
· Continue — IDE AI assistant
· VS Code — Code editor
· Warp — Modern terminal (optional)

---

Built with ❤️ for sovereign engineering.

Document version: 1.0.0 | Last updated: April 2026
