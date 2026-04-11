# Quick Start Guide

Get Gebeta Sovereign Code Assistant running in 10 minutes.

## Prerequisites

- 8GB RAM minimum (16GB recommended)
- 10GB free storage
- VS Code installed

## Step 1: Install Ollama

**macOS/Linux:**
```bash
curl -fsSL https://ollama.com/install.sh | sh
```

Windows: Download from https://ollama.com

Step 2: Start Ollama and Pull Models

```bash
# Start the server
ollama serve

# In another terminal
ollama pull qwen2.5-coder:7b
```

Step 3: Install Continue Extension

1. Open VS Code
2. Go to Extensions (Ctrl+Shift+X)
3. Search "Continue"
4. Click Install

Step 4: Configure Continue

```bash
# macOS/Linux
mkdir -p ~/.continue
cp configs/continue-config.yaml ~/.continue/config.yaml

# Windows
mkdir %USERPROFILE%\.continue
copy configs\continue-config.yaml %USERPROFILE%\.continue\config.yaml
```

Step 5: Test It

Open VS Code, open Continue sidebar, switch to Agent Mode, and type:

```
@agent Create a file called hello.py that prints "Hello Gebeta"
```

Approve the action when prompted.

Done!

You're now coding with local, sovereign AI assistance.

```
