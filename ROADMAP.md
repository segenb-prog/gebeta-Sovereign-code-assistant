# Product Roadmap

## Overview

This roadmap outlines the planned evolution of Gebeta Sovereign Code Assistant.

## Version 1.0 — Foundation (Current) ✅

**Focus:** Documentation, configurations, and starter kit

**Deliverables:**
- Complete documentation (README, security, team deployment, use cases)
- Four pre-configured Continue YAML files (standard, safe, team, low-RAM)
- Project guardrails (`gebeta-rules.md`)
- Safe command policy
- Quick start guide
- MIT License
- GitHub repository structure

**Status:** Complete

---

## Version 1.0.1 — Security & Stability (Released) ✅

**Focus:** Rate limiting, token refresh, error handling

**Deliverables:**
- Rate limiting for authentication endpoints (10 req/min login, 5 req/5min register)
- Token refresh endpoint and React interceptor
- Standardised error responses (FastAPI + Spring Boot)
- React ErrorBoundary component
- Spring Boot CORS and global exception handling

**Status:** Released

---

## Version 2.0 — Platform (Q3 2026)

**Focus:** Web portal, onboarding, analytics

**Planned features:**
- Web-based configuration generator
- One-click onboarding scripts
- Usage analytics dashboard (local-only, opt-in)
- Model performance comparison tool
- Community template gallery
- VS Code extension enhancements

**Target users:** Teams of 3-20 developers

**Status:** Planning

---

## Version 2.1 — Community Templates (Q4 2026)

**Focus:** User-contributed templates and rules

**Planned features:**
- Community-submitted starter templates
- Shared rule packs
- Template gallery

**Target users:** Open source community

**Status:** Roadmap

---

## Version 3.0 — Enterprise (Q1 2027)

**Focus:** Team control plane, governance, audit dashboard

**Planned features:**
- Centralized team configuration management
- Policy-as-code for AI agent behavior
- Audit dashboard with visual logs
- Compliance report generator
- Role-based access controls
- Air-gapped deployment bundles
- Enterprise support and SLAs

**Target users:** Enterprises, regulated industries, government

**Status:** Roadmap

---

## Future Exploration

### Potential Features (Under Consideration)

| Feature | Description | Priority |
|---------|-------------|----------|
| Local embeddings | RAG on your codebase | High |
| Custom model fine-tuning | Train on your code style | Medium |
| Multi-model orchestration | Route tasks to best model | Medium |
| IDE support expansion | PyCharm, IntelliJ, Neovim | High |
| CI/CD integration | Automated PR reviews | Medium |
| Secrets detection | Scan for exposed keys | High |
| Dependency vulnerability scan | Local CVE checking | Medium |

### Community-Requested Features

*Submit feature requests via GitHub Issues*

---

## Timeline Summary

| Version | Focus | Target Date |
|---------|-------|-------------|
| V1.0 | Foundation | ✅ April 2026 |
| V1.0.1 | Security & Stability (rate limiting, token refresh, error handling) | ✅ April 2026 |
| V2.0 | Platform | Q3 2026 |
| V2.1 | Community templates | Q4 2026 |
| V3.0 | Enterprise | Q1 2027 |

---

## How to Contribute

We welcome contributions to help accelerate this roadmap:

- 🐛 Report bugs
- 💡 Suggest features
- 📝 Improve documentation
- 🔧 Submit code
- 🌟 Star the repository

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

---

**Last updated:** April 2026