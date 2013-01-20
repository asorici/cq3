\documentclass[a4paper,10pt]{article}
\usepackage[landscape]{geometry}
\usepackage[utf8x]{inputenc}
\usepackage{multirow}
\begin{document}
\par {\huge \textbf{AI-MAS Winter Olympics: Crafting Quest 2}} \\
{\large Team \textit {{thisteamname}} Report} \\

\label{sec:results}
\section{Interpreting Results}

\par Section \ref{sec:overview} presents a general overview of the results and your team's position on the board. The score in Table \ref{table:overview} is computed by the following rules:
\begin{itemize}
	\item Each win is worth 2 points. A game is won by the team obtaining the highest score.
	\item Each draw is worth 1 point. A game is considered a draw if both teams obtain the same score.
	\item Each lost game is worth 0 points, the loosing team being the one obtaining the lowest score.
\end{itemize}

\par Sections \ref{sec:mapv1.cqm}-\ref{sec:mapv2.cqm} give you the results obtained on each map. Each row contains the results obtained by a team against all other teams, starting from Player \#1's position. Each column contains the results obtained by a team against all other teams, starting from Player \#2's position. The score in bold represents the winning team.

\par Table \ref{table:average} shows the average battle score.

\label{sec:overview}
\section{Qualification Round Overview (2011.02.12)}

\begin{table}[ht]
\begin{minipage}[b]{0.5\linewidth}\centering
\caption{Testing Round \#3 Overview}
\begin{tabular}{|c|c|c|c|c|}
\hline
\textbf{Team No.} & \textbf{W} & \textbf{D} & \textbf{L} & \textbf{Score} \\
\hline
\hline
{% for teamitem in teamscores %} 
        {{ teamitem.name }} & {{ teamitem.w }} & {{ teamitem.d }} & {{ teamitem.l }} & {{ teamitem.s }} \\
        \hline  
{% endfor %}

\end{tabular}
\label{table:overview}
\end{minipage}
\hspace{0.5cm}
\begin{minipage}[b]{0.5\linewidth}\centering
\caption{Testing Round \#3 Average Scores}
\label{table:average}
\begin{tabular}{|c|c|}
\hline
\textbf{Team No.} & \textbf{Average Score} \\
\hline
\hline
{% for teamitem in teamscores %}
        {{ teamitem.name }} & {{ teamitem.averagescore }} \\
        \hline  
{% endfor %}

\end{tabular}
\end{minipage}
\end{table}


\newpage
\section{Results for map.cqm}
\label{sec:map.cqm}


\begin{table}[!htb]
\caption{Results for map.cqm}
\centering

\begin{tabular}{|c||{% for teamitem in teamscores %}c|{% endfor %} }
\hline
{% for teamitem in teamscores %} & \textbf {{teamitem.name}} {% endfor %} \\
\hline
\hline

{% for teamitem in teamscores %} \textbf {{teamitem.name}} 
    {% for game in teamitem.games.map %} {% if game %} & {{ game.me }} / {{ game.op }} {% else %} & -- {% endif %} {% endfor %} \\
    \hline
{% endfor %} 

\end{tabular}
\label{table:map.cqm}
\end{table}

\section{Map configuration}
\label{sec:mapconfig}

\begin{table}[!htb]
\caption{Resources}
\centering
\begin{tabular}{|c||c|c|c|c|c|c|c|c|c|c|c|c|}
\hline
\textbf{Resource} & R1 & R2 & R3 & R4 & R5 & R6 & R7 & R8 & R9 & R10 & R11 & R12\\
\hline
\textbf{Quantity} & 502 & 392 & 1670 & 892 & 700 & 1872 & 196 & 885 & 1904 & 1706 & 1205 & 1193\\
\hline
\end{tabular}
\label{table:resmap.cqm}
\end{table}


\begin{table}[!htb]
\caption{Objects}
\centering
\begin{tabular}{|c||c|c|c|c|c|c|c|c|c|c|c|c|}
\hline
\textbf{Object} & O1 & O2 & O3 & O4 & O5 & O6 & O7 & O8 & O9 & O10 & O11 & O12\\
\hline
\textbf{Value} & 166 & 3942 & 4003 & 368 & 324 & 179 & 312 & 3738 & 182 & 272 & 196 & 380\\
\hline
\end{tabular}
\label{table: objmap.cqm}
\end{table}


\end{document}
