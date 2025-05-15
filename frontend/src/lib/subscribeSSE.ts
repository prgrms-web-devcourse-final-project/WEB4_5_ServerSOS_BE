export async function subscribeSSE({
  url,
  onMessage,
  onError,
  token,
}: {
  url: string
  onMessage: (data: any) => void
  onError: (error: any) => void
  token: string
}) {
  try {
    const response = await fetch(url, {
      headers: {
        Authorization: `Bearer ${token}`,
        Accept: "text/event-stream",
      },
    })

    if (!response.body) throw new Error("No response body")

    const reader = response.body.getReader()
    const decoder = new TextDecoder("utf-8")
    let buffer = ""

    while (true) {
      const { value, done } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })

      let lines = buffer.split("\n")
      buffer = lines.pop() || ""

      for (const line of lines) {
        if (line.startsWith("data:")) {
          try {
            const data = JSON.parse(line.replace(/^data:\s*/, ""))
            onMessage(data)
          } catch (e) {
            // 파싱 에러 처리
          }
        }
      }
    }
  } catch (err) {
    if (onError) onError(err)
  }
}
