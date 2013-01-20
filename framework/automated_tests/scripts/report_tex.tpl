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
	\item Each win is worth 1 points. A game is won by the team obtaining the highest score.
	\item Each draw is worth 1 point. A game is considered a draw if both teams obtain the same score.
	\item Each lost game is worth 0 points, the loosing team being the one obtaining the lowest score.
\end{itemize}

\par Sections \ref{sec:mapv1.cqm}-\ref{sec:mapv2.cqm} give you the results obtained on each map. Each row contains the results obtained by a team against all other teams, starting from Player \#1's position. Each column contains the results obtained by a team against all other teams, starting from Player \#2's position. The score in bold represents the winning team.

\par Table \ref{table:average} shows the average battle score.

\label{sec:overview}
\section{Testing Round \#2 Overview (2011.01.22)}

\begin{table}[ht]
\begin{minipage}[b]{0.5\linewidth}\centering
\caption{Testing Round \#3 Overview}
\begin{tabular}{|c|c|c|c|c|}
\hline
\textbf{Team No.} & \textbf{W} & \textbf{D} & \textbf{L} & \textbf{Score} \\
\hline
\hline
{% for teamitem in teamscores %} 
    {% ifequal teamitem.id thisteamid %} 
        {{ teamitem.name }} & {{ teamitem.w }} & {{ teamitem.d }} & {{ teamitem.l }} & {{ teamitem.s }} \\
        \hline  
    {% else %}
        Team \#{{forloop.counter}} & {{ teamitem.w }} & {{ teamitem.d }} & {{ teamitem.l }} & {{ teamitem.s }} \\
        \hline  
    {% endifequal %}
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
    {% ifequal teamitem.id thisteamid %} 
        {{ teamitem.name }} & {{ teamitem.averagescore }} \\
        \hline  
    {% else %}
        Team \#{{forloop.counter}} & {{ teamitem.averagescore }} \\
        \hline  
    {% endifequal %}
{% endfor %}

\end{tabular}
\end{minipage}
\end{table}


\newpage
\section{Results for mapv1.cqm}
\label{sec:mapv1.cqm}


\begin{table}[!htb]
\caption{Results for mapv1.cqm}
\centering

\begin{tabular}{|c||{% for teamitem in teamscores %}c|{% endfor %} }
\hline
{% for teamitem in teamscores %}{% ifequal teamitem.id thisteamid %} & \textbf {{teamitem.name}} {% else %} & \textbf {{forloop.counter}} {% endifequal%} {% endfor %} \\
\hline
\hline

{% for teamitem in teamscores %}{% ifequal teamitem.id thisteamid %} \textbf {{teamitem.name}} {% else %} \textbf {{forloop.counter}} {% endifequal %}
    {% for game in teamitem.games.map_v1 %} {% if game %} & {{ game.me }} / {{ game.op }} {% else %} & -- {% endif %} {% endfor %} \\
    \hline
{% endfor %} 

\end{tabular}
\label{table:mapv1.cqm}
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
\textbf{Quantity} & 504 & 674 & 952 & 689 & 690 & 908 & 183 & 383 & 159 & 504 & 1227 & 976\\
\hline
\end{tabular}
\label{table:resmapv1.cqm}
\end{table}


\begin{table}[!htb]
\caption{Objects}
\centering
\begin{tabular}{|c||c|c|c|c|c|c|c|c|c|c|c|c|}
\hline
\textbf{Object} & O1 & O2 & O3 & O4 & O5 & O6 & O7 & O8 & O9 & O10 & O11 & O12\\
\hline
\textbf{Value} & 420 & 316 & 295 & 3809 & 310 & 314 & 286 & 5735 & 340 & 4516 & 313 & 253\\
\hline
\end{tabular}
\label{table: objmapv1.cqm}
\end{table}

\newpage
\section{Results for mapv2.cqm}
\label{sec:mapv2.cqm}

\begin{table}[htb]
\caption{Results for mapv2.cqm}
\centering
\begin{tabular}{|c||{% for teamitem in teamscores %}c|{% endfor %} }
\hline
{% for teamitem in teamscores %}{% ifequal teamitem.id thisteamid %} & \textbf {{teamitem.name}} {% else %} & \textbf {{forloop.counter}} {% endifequal%} {% endfor %} \\
\hline
\hline

{% for teamitem in teamscores %}{% ifequal teamitem.id thisteamid %} \textbf {{teamitem.name}} {% else %} \textbf {{forloop.counter}} {% endifequal %}
    {% for game in teamitem.games.map_v2 %} {% if game %} & {{ game.me }} / {{ game.op }} {% else %} & -- {% endif %} {% endfor %} \\
    \hline
{% endfor %}
\end{tabular}
\label{table:mapv2.cqm}
\end{table}

\section{Map configuration}
\label{sec:mapconfig2}

\begin{table}[htb]
\caption{Resources}
\centering
\begin{tabular}{|c||c|c|c|c|c|c|c|c|c|c|c|c|}
\hline
\textbf{Resource} & R1 & R2 & R3 & R4 & R5 & R6 & R7 & R8 & R9 & R10 & R11 & R12\\
\hline
\textbf{Quantity} & 491 & 196 & 1822 & 1400 & 196 & 894 & 1001 & 390 & 896 & 1008 & 693 & 196\\
\hline
\end{tabular}
\label{table:resmapv2.cqm}
\end{table}


\begin{table}[htb]
\caption{Objects}
\centering
\begin{tabular}{|c||c|c|c|c|c|c|c|c|c|c|c|c|}
\hline
\textbf{Object} & O1 & O2 & O3 & O4 & O5 & O6 & O7 & O8 & O9 & O10 & O11 & O12\\
\hline
\textbf{Value} & 6515 & 489 & 338 & 290 & 5276 & 386 & 6012 & 374 & 362 & 262 & 241 & 356\\
\hline
\end{tabular}
\label{table: objmapv2.cqm}
\end{table}

\end{document}
