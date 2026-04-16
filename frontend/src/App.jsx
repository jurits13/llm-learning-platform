import {useEffect, useState} from 'react'
import axios from 'axios'
import './App.css'

const api = axios.create({
    baseURL: '',
})

function App() {
    const [username, setUsername] = useState('')
    const [user, setUser] = useState(null)

    const [sessionForm, setSessionForm] = useState({
        title: '',
        problemDescription: '',
        codeSnippet: '',
        whatTried: '',
    })

    const [session, setSession] = useState(null)
    const [messages, setMessages] = useState([])
    const [messageInput, setMessageInput] = useState('')
    const [creatingUser, setCreatingUser] = useState(false)
    const [creatingSession, setCreatingSession] = useState(false)
    const [sendingMessage, setSendingMessage] = useState(false)
    const [error, setError] = useState('')

    async function createUser(e) {
        e.preventDefault()
        if (!username.trim() || creatingUser) return

        setCreatingUser(true)
        setError('')

        try {
            const res = await api.post('/api/users', {
                username: username.trim(),
                role: 'STUDENT',
            })
            setUser(res.data)
            setUsername('')
        } catch (err) {
            setError(readError(err))
        } finally {
            setCreatingUser(false)
        }
    }

    async function createSession(e) {
        e.preventDefault()
        if (!user || creatingSession) return

        setCreatingSession(true)
        setError('')

        try {
            const res = await api.post('/api/help-sessions', {
                userId: user.id,
                title: sessionForm.title.trim(),
                problemDescription: sessionForm.problemDescription.trim(),
                codeSnippet: sessionForm.codeSnippet,
                whatTried: sessionForm.whatTried,
            })
            setSession(res.data)
            setMessages([])
        } catch (err) {
            setError(readError(err))
        } finally {
            setCreatingSession(false)
        }
    }

    async function loadMessages(sessionId) {
        try {
            const res = await api.get(`/api/help-sessions/${sessionId}/messages`)
            setMessages(res.data)
        } catch (err) {
            setError(readError(err))
        }
    }

    async function sendMessage(e) {
        e.preventDefault()
        if (!session || !messageInput.trim() || sendingMessage) return

        setSendingMessage(true)
        setError('')

        try {
            const studentText = messageInput.trim()
            setMessageInput('')

            await api.post(`/api/help-sessions/${session.id}/messages`, {
                content: studentText,
            })

            await loadMessages(session.id)
        } catch (err) {
            setError(readError(err))
        } finally {
            setSendingMessage(false)
        }
    }

    useEffect(() => {
        if (session?.id) {
            loadMessages(session.id)
        }
    }, [session?.id])

    return (
        <div className="app">
            <div className="container">
                <h1>LLM Learning Coach</h1>
                <p className="subtitle">
                    Web development learning support with coaching-style LLM responses
                </p>

                {error && <div className="error">{error}</div>}

                {!user && (
                    <section className="card">
                        <h2>1. Create student user</h2>
                        <form onSubmit={createUser} className="form">
                            <input
                                type="text"
                                placeholder="Username"
                                value={username}
                                disabled={creatingUser}
                                onChange={(e) => setUsername(e.target.value)}
                            />
                            <button type="submit" disabled={creatingUser || !username.trim()}>
                                {creatingUser ? 'Creating user...' : 'Create user'}
                            </button>
                        </form>
                    </section>
                )}

                {user && !session && (
                    <section className="card">
                        <h2>2. Create help session</h2>
                        <p className="muted">
                            Logged in as: <strong>{user.username}</strong>
                        </p>

                        <form onSubmit={createSession} className="form vertical">
                            <input
                                type="text"
                                placeholder="Session title"
                                value={sessionForm.title}
                                disabled={creatingSession}
                                onChange={(e) =>
                                    setSessionForm({...sessionForm, title: e.target.value})
                                }
                            />

                            <textarea
                                placeholder="Describe the problem"
                                rows="4"
                                value={sessionForm.problemDescription}
                                disabled={creatingSession}
                                onChange={(e) =>
                                    setSessionForm({
                                        ...sessionForm,
                                        problemDescription: e.target.value,
                                    })
                                }
                            />

                            <textarea
                                placeholder="Paste code snippet (optional)"
                                rows="8"
                                value={sessionForm.codeSnippet}
                                disabled={creatingSession}
                                onChange={(e) =>
                                    setSessionForm({...sessionForm, codeSnippet: e.target.value})
                                }
                            />

                            <textarea
                                placeholder="What have you already tried? (optional)"
                                rows="4"
                                value={sessionForm.whatTried}
                                disabled={creatingSession}
                                onChange={(e) =>
                                    setSessionForm({...sessionForm, whatTried: e.target.value})
                                }
                            />

                            <button
                                type="submit"
                                disabled={
                                    creatingSession ||
                                    !sessionForm.title.trim() ||
                                    !sessionForm.problemDescription.trim()
                                }
                            >
                                {creatingSession ? 'Creating session...' : 'Create session'}
                            </button>
                        </form>
                    </section>
                )}

                {user && session && (
                    <>
                        <section className="card">
                            <h2>Session</h2>
                            <p><strong>Student:</strong> {user.username}</p>
                            <p><strong>Title:</strong> {session.title}</p>
                            <p><strong>Problem:</strong> {session.problemDescription}</p>

                            {session.codeSnippet && (
                                <>
                                    <p><strong>Code:</strong></p>
                                    <pre>{session.codeSnippet}</pre>
                                </>
                            )}
                        </section>

                        <section className="card chat-card">
                            <h2>3. Chat with the coach</h2>

                            <div className="messages">
                                {messages.length === 0 && (
                                    <div className="empty">No messages yet.</div>
                                )}

                                {messages.map((msg) => (
                                    <div
                                        key={msg.id}
                                        className={`message ${msg.role === 'STUDENT' ? 'student' : 'coach'}`}
                                    >
                                        <div className="message-header">
                                            <strong>{msg.role}</strong>

                                            {msg.role === 'COACH' && msg.coachResponseLevel && (
                                                <span className="badge">{msg.coachResponseLevel}</span>
                                            )}

                                            {msg.role === 'COACH' && msg.filteredByPolicy && (
                                                <span className="badge warning">FILTERED</span>
                                            )}
                                        </div>

                                        <div className="message-content whitespace">
                                            {msg.content}
                                        </div>

                                        {msg.role === 'COACH' && (
                                            <div className="message-meta">
                                                <span>Model: {msg.llmModel || '-'}</span>
                                                <span>Prompt: {msg.promptVersion || '-'}</span>
                                                <span>Policy: {msg.policyReason || '-'}</span>
                                            </div>
                                        )}
                                    </div>
                                ))}
                            </div>

                            <form onSubmit={sendMessage} className="form vertical">
    <textarea
        rows="4"
        placeholder="Ask the coach a follow-up question..."
        value={messageInput}
        disabled={sendingMessage}
        onChange={(e) => setMessageInput(e.target.value)}
    />
                                <button type="submit" disabled={sendingMessage || !messageInput.trim()}>
                                    {sendingMessage ? 'Sending...' : 'Send message'}
                                </button>
                            </form>
                        </section>
                    </>
                )}
            </div>
        </div>
    )
}

function readError(err) {
    return (
        err?.response?.data?.error ||
        err?.response?.data?.message ||
        err?.message ||
        'Something went wrong'
    )
}

export default App