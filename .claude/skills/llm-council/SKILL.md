---
name: llm-council
description: Run a question through an LLM Council - five independent advisors answer separately, review each other's anonymized answers and rank them, then a Chairman synthesizes one final answer. Use for decisions, trade-offs, and hard questions where multiple perspectives help. Invoke as /llm-council <question>.
argument-hint: <your question or decision>
disable-model-invocation: true
allowed-tools: Agent
---

# LLM Council

Port of the 3-stage council from https://github.com/karpathy/llm-council (`backend/council.py`), running on Claude Code subagents instead of OpenRouter. No API key is needed. The logic is unchanged: independent first opinions, anonymized peer ranking, Chairman synthesis.

The question is: $ARGUMENTS

If `$ARGUMENTS` is empty, ask the user for the question and stop.

## Rules

- Advisors in Stage 1 must NOT see each other's answers or the roles of others.
- Run every stage's subagents in parallel: one message, several `Agent` calls (`subagent_type: general-purpose`).
- Tell every subagent not to use any tools and to answer from reasoning alone, so the council stays a pure opinion process.
- All advisors are the same underlying model. The roles create diversity of perspective, not of model. Say so in the final output.
- If an advisor or reviewer fails, drop it and continue. If every Stage 1 advisor fails, reply "All advisors failed to respond. Please try again." and stop.

## Stage 1: First opinions

Spawn five subagents in parallel. Each gets only the question plus its own role line below, and is asked for a direct, complete answer.

1. **Realist**: practical, evidence-based, what actually works in the real world.
2. **Skeptic**: attacks assumptions, hunts for flaws, risks, and failure modes.
3. **Optimist**: finds upside, opportunities, and the best realistic outcome.
4. **Domain Expert**: answers with deep, technically precise subject knowledge.
5. **First-Principles Thinker**: ignores convention, rebuilds the answer from fundamentals.

Prompt template per advisor:

```
You are the {ROLE} on a council of advisors. Your lens: {ROLE DESCRIPTION}.
Answer the question below independently and completely from that perspective. Do not use tools.

Question: {QUESTION}
```

Collect results as a list of `{role, response}`.

## Stage 2: Anonymized review

1. Shuffle the successful Stage 1 responses once, then label them `Response A`, `Response B`, ... in shuffled order. Keep a private `label_to_role` mapping. Never put role names, or any hint of authorship, into the reviewer prompts.
2. Spawn one reviewer per successful advisor, in parallel, all with this exact prompt (each reviewer ranks all responses, including its own, as in the original):

```
You are evaluating different responses to the following question:

Question: {QUESTION}

Here are the responses from different advisors (anonymized):

{for each: "Response {LABEL}:\n{RESPONSE TEXT}"  joined by blank lines}

Your task:
1. First, evaluate each response individually. For each response, explain what it does well and what it does poorly.
2. Then, at the very end of your response, provide a final ranking.

IMPORTANT: Your final ranking MUST be formatted EXACTLY as follows:
- Start with the line "FINAL RANKING:" (all caps, with colon)
- Then list the responses from best to worst as a numbered list
- Each line should be: number, period, space, then ONLY the response label (e.g., "1. Response A")
- Do not add any other text or explanations in the ranking section

Example of the correct format for your ENTIRE response:

Response A provides good detail on X but misses Y...
Response B is accurate but lacks depth on Z...
Response C offers the most comprehensive answer...

FINAL RANKING:
1. Response C
2. Response A
3. Response B

Do not use tools. Now provide your evaluation and ranking:
```

3. Parse each reviewer's text after `FINAL RANKING:` for `N. Response X` lines (fallback: every `Response X` in order). 
4. Aggregate: for each response, average its position across all reviewers that ranked it (lower is better). Show the arithmetic. Map labels back to roles with `label_to_role` only now, after review is finished.

## Stage 3: Chairman synthesis

Spawn one subagent as Chairman with this prompt (fill in Stage 1 by role and Stage 2 reviewer texts; also include the aggregate ranking, de-anonymized):

```
You are the Chairman of an LLM Council. Multiple advisors have provided responses to a user's question, and then ranked each other's responses.

Original Question: {QUESTION}

STAGE 1 - Individual Responses:
{for each: "Advisor: {ROLE}\nResponse: {RESPONSE}"}

STAGE 2 - Peer Rankings:
{for each reviewer: "Reviewer: {n}\nRanking: {FULL REVIEW TEXT}"}

Aggregate ranking (average position, lower is better, labels resolved to roles):
{AGGREGATE}

Your task as Chairman is to synthesize all of this information into a single, comprehensive, accurate answer to the user's original question. Consider:
- The individual responses and their insights
- The peer rankings and what they reveal about response quality
- Any patterns of agreement or disagreement

Provide a clear, well-reasoned final answer that represents the council's collective wisdom. Do not use tools.
```

If the Chairman fails, output "Error: Unable to generate final synthesis." and still show Stages 1 and 2.

## Output to the user

1. **Final answer** (Chairman's synthesis), first and prominent.
2. **Aggregate ranking** table: role, average rank, number of rankings, with the calculation.
3. **Stage 1** one short paragraph per advisor role (offer full text on request).
4. **Disagreements**: where advisors or reviewers split.
5. One line: "All five advisors are the same underlying model with different role prompts."
