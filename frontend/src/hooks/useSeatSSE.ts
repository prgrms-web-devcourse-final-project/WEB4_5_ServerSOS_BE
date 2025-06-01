// import {useEffect, useRef} from "react"
// import {EventSourcePolyfill} from "event-source-polyfill"
// import {BACKEND_API} from "@/api/apiClient.ts";
// import {getLoginInfo} from "@/lib/storage/loginStorage"
//
// export const useSeatSSE = ({
//                                sessionId,
//                                entryToken,
//                                onSeatUpdate,
//                            }: {
//     sessionId: number
//     entryToken: string
//     onSeatUpdate: (data: {
//         row: number
//         col: number
//         areaId: number
//         status: "available" | "reserved"
//     }) => void
// }) => {
//     const eventSourceRef = useRef<EventSource | null>(null)
//
//     useEffect(() => {
//         const loginInfo = getLoginInfo()
//         const accessToken = loginInfo?.token || ""
//
//         console.log("SSE useEffect triggered")
//         console.log("sessionId:", sessionId)
//         console.log("accessToken:", accessToken)
//         console.log("entryToken:", entryToken)
//
//         if (!sessionId || !accessToken || !entryToken) return
//
//         const url = `${BACKEND_API}/api/areas/subscribe?sessionId=${sessionId}`
//
//         const source = new EventSourcePolyfill(url, {
//             headers: {
//                 Authorization: `Bearer ${accessToken}`,
//                 entryAuth: `Bearer ${entryToken}`,
//             },
//             withCredentials: true,
//         })
//
//         eventSourceRef.current = source
//
//         source.onmessage = (event: { data: string; }) => {
//             try {
//                 const data = JSON.parse(event.data)
//                 onSeatUpdate(data)
//             } catch (err) {
//                 console.error("Invalid SSE data:", event.data)
//             }
//         }
//
//         source.onerror = (err: any) => {
//             console.error("SSE connection error", err)
//             source.close()
//         }
//
//         return () => {
//             source.close()
//         }
//     }, [sessionId, entryToken])
// }
